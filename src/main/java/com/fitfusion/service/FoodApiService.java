package com.fitfusion.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitfusion.dto.FoodDto;
import com.fitfusion.dto.FoodSaveResult;
import com.fitfusion.enums.FoodSaveStatus;
import com.fitfusion.mapper.FoodMapper;
import com.fitfusion.util.ValueWithUnitApplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor

// 공공 API → 음식DB 저장을 위한 서비스 (관리자를 위한 기능)
public class FoodApiService {

    private final FoodMapper foodMapper;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${mfds.fooddb.apiKey}")
    private String serviceKey;

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    // =================== 설정 상수 =================== //
    private static final String BASE_URL =
            "https://apis.data.go.kr/1471000/FoodNtrCpntDbInfo02/getFoodNtrCpntDbInq02"; //API 요청 URL

    private static final String DEFAULT_DATA_SOURCE = "식품영양성분DB";

    // 공공 API 호출 시 제약 조건 설정
    private static final int PAGE_SIZE = 100;               // 한 페이지당 데이터 개수 (공공 API 규정)
    private static final int MAX_PAGES_PER_KEYWORD = 1000;  // 키워드별 최대 페이지
    private static final int MAX_API_REQUESTS_PER_DAY = 10_000; // 하루 최대 API 호출 횟수 (공공 API 규정)
    private static final int MAX_RECORDS_PER_RUN = 5000;    // 한 번 실행 시 최대 저장 레코드 수
    private static final int DELAY_MILLIS = 200;            // API 호출 간 대기 시간(ms)

    // ========================================================
    // [1] API 데이터 조회
    // ========================================================

    public List<FoodDto> fetchPage(String keyword, int page) {
        return fetchFoods(keyword, page, PAGE_SIZE);
    }

    public List<FoodDto> fetchPreview(String keyword) {
        return fetchPage(keyword, 1);
    }

    public List<FoodDto> fetchAllPages(String keyword) {
        List<FoodDto> allData = new ArrayList<>();
        log.info("[{}] 데이터 수집 시작", keyword);

        for (int page = 1; page <= MAX_PAGES_PER_KEYWORD; page++) {
            List<FoodDto> pageData = fetchFoods(keyword, page, PAGE_SIZE);
            if (pageData.isEmpty()) break;

            allData.addAll(pageData);
            if (log.isDebugEnabled()) {
                log.debug("[{}] {}페이지 수집 완료 → {}건", keyword, page, pageData.size());
            }
        }

        log.info("[{}] 데이터 수집 종료 → 총 {}건", keyword, allData.size());
        return allData;
    }

    private List<FoodDto> fetchFoods(String keyword, int page, int size) {
        try {
            String url = buildApiUrl(keyword, page, size);

            if (log.isDebugEnabled()) {
                log.debug("[{}] {}페이지 요청 URL: {}", keyword, page, maskApiKey(url));
            }



            JsonNode body = callApi(url);
            JsonNode items = body.path("items");
            int totalCount = body.path("totalCount").asInt(-1);

            if (!items.isArray()) {
                if (totalCount <= 0) {
                    log.info("[{}] 검색 결과 없음", keyword);
                } else if (page > 1) {
                    log.info("[{}] 데이터 없음 → {}페이지 이후 종료 (총 {}건)", keyword, page, totalCount);
                } else {
                    log.warn("[{}] 첫 페이지인데 items 없음 (totalCount={})", keyword, totalCount);
                }
                return List.of();
            }

            return parseApiItems(items);

        } catch (Exception e) {
            log.error("[{}] API 호출 실패: {}", keyword, e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * 특정 키워드로 전체 데이터 개수 조회
     */
    public int getTotalCount(String keyword) {
        try {
            String url = buildApiUrl(keyword, 1, 1);
            JsonNode body = callApi(url);
            return body.path("totalCount").asInt(0);
        } catch (Exception e) {
            log.error("totalCount 조회 실패: {}", e.getMessage(), e);
            return 0;
        }
    }

    /**
     * API 전체 데이터 개수 조회
     */
    public int getTotalDataCount() {
        return getTotalCount("");
    }

    // ========================================================
    // [2] DB 저장
    // ========================================================

    public FoodSaveStatus saveFood(FoodDto food) {
        if (!isValidApiData(food)) {
            log.warn("유효성 검증 실패 → 저장 생략: {}({}) [{}]",
                    food.getFoodName(), food.getFoodCode(), getValidationErrors(food));
            return FoodSaveStatus.INVALID;
        }

        if (isDuplicate(food)) {
            log.debug("중복 데이터 → 저장 생략: {}({})", food.getFoodName(), food.getFoodCode());
            return FoodSaveStatus.DUPLICATE;
        }

        try {
            applyInsertDefaults(food);
            foodMapper.insertFood(food);
            return FoodSaveStatus.SUCCESS;
        } catch (Exception e) {
            log.error("저장 실패: {}({})", food.getFoodName(), food.getFoodCode(), e);
            return FoodSaveStatus.ERROR;
        }
    }

    public FoodSaveResult saveFoods(List<FoodDto> foods) {
        int saved = 0, duplicated = 0, failed = 0;

        for (FoodDto food : foods) {
            FoodSaveStatus status = saveFood(food);
            switch (status) {
                case SUCCESS -> saved++;
                case DUPLICATE -> duplicated++;
                case INVALID, ERROR -> failed++;
            }
        }

        log.info("저장 결과 → 성공: {} | 중복: {} | 실패: {}", saved, duplicated, failed);
        return new FoodSaveResult(saved, duplicated, failed);
    }

    public String importByKeywords(List<String> keywords) {
        int totalSaved = 0, totalRequests = 0, totalReceived = 0;
        int saved = 0, duplicated = 0, failed = 0;

        for (String keyword : keywords) {
            int totalCount = getTotalCount(keyword);
            log.info("[{}] 총 {}건 → 데이터 수집 시작", keyword, totalCount);

            for (int page = 1; page <= MAX_PAGES_PER_KEYWORD; page++) {
                if (totalSaved >= MAX_RECORDS_PER_RUN || totalRequests >= MAX_API_REQUESTS_PER_DAY) {
                    log.warn("요청 한도 초과 → 저장: {}, 요청: {}", totalSaved, totalRequests);
                    break;
                }

                List<FoodDto> list = fetchPage(keyword, page);
                totalRequests++;

                if (list.isEmpty()) break;

                totalReceived += list.size();
                FoodSaveResult result = saveFoods(list);
                saved += result.getSaved();
                duplicated += result.getDuplicated();
                failed += result.getFailed();
                totalSaved += result.getSaved();

                try {
                    Thread.sleep(DELAY_MILLIS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return "작업 중단";
                }
            }
        }

        String summary = String.format("""
                수집 완료
                - 응답 항목 수: %,d
                - 저장 성공: %,d
                - 중복 제외: %,d
                - 저장 실패: %,d
                - 요청 횟수: %,d
                """, totalReceived, saved, duplicated, failed, totalRequests);

        log.info(summary);
        return summary;
    }

    // ========================================================
    // [3] 유틸리티
    // ========================================================

    private JsonNode callApi(String url) throws Exception {
        String json = restTemplate.getForObject(url, String.class);
        if (json != null && json.trim().startsWith("<")) {
            throw new RuntimeException("API 인증 실패 또는 ServiceKey 오류");
        }
        return objectMapper.readTree(json).path("body");
    }

    private String buildApiUrl(String keyword, int page, int size) {
        String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
        return BASE_URL + "?serviceKey=" + serviceKey
                + "&type=json"
                + "&pageNo=" + page
                + "&numOfRows=" + size
                + "&FOOD_NM_KR=" + encodedKeyword;
    }

    private String maskApiKey(String url) {
        // 개발용(dev), 로컬(local), 기본(default) 환경에서는 API 키 그대로 보여줌
        if ("dev".equals(activeProfile) || "local".equals(activeProfile) || "default".equals(activeProfile)) {
            return url;
        }
        // 그 외 환경에서는 API 키를 *****로 가림
        return url.replaceAll("serviceKey=[^&]+", "serviceKey=****");
    }

    private List<FoodDto> parseApiItems(JsonNode items) {
        List<FoodDto> result = new ArrayList<>();
        if (items.isArray()) {
            for (JsonNode item : items) {
                try {
                    FoodDto food = objectMapper.treeToValue(item, FoodDto.class);

                    //ServingSize 파싱 (STRICT: 없거나 단위 이상 → insert 스킵)
                    boolean validServingSize = ValueWithUnitApplier.applyServingSizeValue(
                            item.path("SERVING_SIZE").asText(null),
                            food::setFoodServingSizeValue,
                            food::setFoodServingSizeUnit
                    );
                    if (!validServingSize) {
                        log.warn("[저장 스킵] 유효하지 않은 ServingSize → foodName: {}", food.getFoodName());
                        continue;
                    }

                    //Weight 파싱
                    ValueWithUnitApplier.applyWeightValue(
                            item.path("Z10500").asText(null),
                            food::setFoodWeightValue,
                            food::setFoodWeightUnit
                    );

                    result.add(food);
                } catch (Exception e) {
                    log.warn("[API → DTO 변환 실패] item: {} | 원인: {}", item.toString(), e.getMessage());
                }
            }
        }
        return result;
    }

    private boolean isDuplicate(FoodDto food) {
        return food.getFoodCode() != null && !food.getFoodCode().isBlank()
                && foodMapper.existsByFoodCode(food.getFoodCode()) > 0;
    }

    private void applyInsertDefaults(FoodDto food) {
        if (food.getIsUserCreated() == null) food.setIsUserCreated("N");
        if (food.getIsImported() == null) food.setIsImported("N");
        if (food.getCreatedDate() == null) food.setCreatedDate(new Date());
        if (food.getUpdatedDate() == null) food.setUpdatedDate(new Date());
        if (food.getDataSourceName() == null) food.setDataSourceName(DEFAULT_DATA_SOURCE);
    }

    private boolean isValidApiData(FoodDto food) {
        return getValidationErrors(food).isEmpty();
    }

    private List<String> getValidationErrors(FoodDto food) {
        List<String> errors = new ArrayList<>();

        if (food.getFoodCode() == null || food.getFoodCode().isBlank()) {
            errors.add("식품코드 누락");
        }
        if (food.getFoodName() == null || food.getFoodName().isBlank()) {
            errors.add("식품명 누락");
        }

        // ServingSize는 필수
        if (food.getFoodServingSizeValue() == null) {
            errors.add("ServingSize 값 없음");
        }
        if (food.getFoodServingSizeUnit() == null) {
            errors.add("ServingSize 단위 없음");
        }

        // foodWeight는 optional → 에러 체크하지 않음
        // 필요하면 로그만 남기고 저장 가능

        return errors;
    }
}
