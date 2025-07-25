package com.fitfusion.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitfusion.config.RestTemplateConfig;
import com.fitfusion.dto.FoodDto;
import com.fitfusion.dto.FoodSaveResult;
import com.fitfusion.enums.FoodSaveStatus;
import com.fitfusion.mapper.FoodMapper;
import com.fitfusion.util.UnitMappingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * FoodApiService
 * - 공공데이터 식품 영양정보 API 호출
 * - DB 저장 기능 제공
 * - 키워드 기반 데이터 Import 기능
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FoodApiService {

    private final FoodMapper foodMapper;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${mfds.fooddb.apiKey}")
    private String serviceKey;

    // =================== 설정 상수 =================== //
    private static final String BASE_URL =
            "https://apis.data.go.kr/1471000/FoodNtrCpntDbInfo02/getFoodNtrCpntDbInq02";

    private static final String DEFAULT_DATA_SOURCE = "식품영양성분DB";

    // API 정책 관련 설정
    private static final int MAX_PAGE_SIZE = 100;              // 한 페이지당 최대 조회 건수
    private static final int MAX_API_REQUESTS_PER_DAY = 10_000; // API 일일 요청 제한

    // 커스텀 설정
    private static final int PAGE_SIZE = 100; // 한 페이지당 조회 건수(100 이하 추천)
    private static final int MAX_RECORDS_PER_RUN = 5000; // 1회 실행 시 저장 한도
    private static final int MAX_PAGES_PER_KEYWORD = 1000; // 키워드별 최대 조회 페이지
    private static final int DELAY_MILLIS = 200;  // API 요청 간 대기(ms) → API 서버 부하 방지 및 차단 방지 목적

    // ========================================================
    //  [1] 외부 API 조회 메서드 (fetch 메서드)
    // ========================================================
    /**
     * 특정 페이지 조회
     *
     * @param keyword 검색어 (FOOD_NM_KR)
     * @param page    페이지 번호 (1부터 시작)
     * @return 검색 결과 FoodDto 리스트
     */
    public List<FoodDto> fetchPage(String keyword, int page) {
        return fetchFoods(keyword, page, PAGE_SIZE);
    }

    /**
     * 첫 페이지만 조회 (프리뷰용)
     *
     * @param keyword 검색어 (FOOD_NM_KR)
     * @return 첫 페이지 결과 FoodDto 리스트
     */
    public List<FoodDto> fetchPreview(String keyword) {
        return fetchPage(keyword, 1);
    }

    /**
     * 모든 페이지 조회 (해당 키워드 전체 데이터)
     *
     * @param keyword 검색어 (FOOD_NM_KR)
     * @return 전체 페이지의 FoodDto 리스트
     */
    public List<FoodDto> fetchAllPages(String keyword) {
        List<FoodDto> result = new ArrayList<>();
        for (int page = 1; page <= MAX_PAGES_PER_KEYWORD; page++) {
            List<FoodDto> list = fetchPage(keyword, page);
            if (list.isEmpty()) break;
            result.addAll(list);
        }
        return result;
    }

    /**
     * API 호출 및 응답 파싱
     *
     * @param keyword 검색어
     * @param page    페이지 번호
     * @param size    페이지당 항목 수
     * @return FoodDto 리스트
     */
    private List<FoodDto> fetchFoods(String keyword, int page, int size) {
        try {
            JsonNode body = callApi(keyword, page, size);
            JsonNode items = body.path("items");
            int totalCount = body.path("totalCount").asInt(-1);

            if (!items.isArray()) {

                if (totalCount <=0) {
                    // 전체 검색 결과 없음
                    log.info("📭 '{}' 검색 결과가 없습니다. (totalCount=0)", keyword);
                } else if (page > 1) {
                    // 더 이상 데이터 없음 (정상 흐름)
                    log.info("✅ '{}' 검색 종료: {}페이지에는 데이터 없음 (총 {}건)", keyword, page, totalCount);
                }
                    else {
                    // 첫 페이지인데 items 없음 → 비정상 응답 가능성
                        log.warn("❌ '{}' 검색 결과(totalCount={})인데 items 배열 누락 (응답={})",
                                keyword, totalCount, body.toPrettyString());
                    }
                return List.of();
            }

            // items 배열이 존재 (빈 배열도 포함) → 그대로 파싱 (빈 배열이면 parseApiItems가 빈 리스트 반환)
            return parseApiItems(items);

        } catch (Exception e) {
            log.error("💥 API 호출 실패: {}", e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * 특정 키워드로 조회 가능한 전체 데이터 건수를 반환합니다.
     *
     * @param keyword 검색어 (FOOD_NM_KR)
     * @return 전체 건수 (없으면 0)
     */
    public int getTotalCount(String keyword) {
        try {
            JsonNode body = callApi(keyword, 1, 1); // 1건만 요청 → totalCount 확인
            return body.path("totalCount").asInt(0);
        } catch (Exception e) {
            log.error("💥 totalCount 조회 실패: {}", e.getMessage(), e);
            return 0;
        }
    }

    /**
     * 공공데이터 API 전체 데이터 건수를 반환합니다.
     *
     * @return 전체 데이터 건수 (없으면 0)
     */
    public int getTotalDataCount() {
        return getTotalCount(""); // 빈 키워드로 전체 데이터 요청
    }

    // ========================================================
    // [2] API 데이터 → DB 저장
    // ========================================================

    /**
     * 단일 음식 정보를 DB에 저장합니다.
     * (유효성 검사 및 중복 검사 포함)
     *
     * @param food 저장할 FoodDto
     * @return 저장 상태 (SUCCESS, DUPLICATE, INVALID, ERROR)
     */
    public FoodSaveStatus saveFood(FoodDto food) {
        // 1. 유효성 검사
        if (!isValidApiData(food)) {
            log.warn("❌ 유효성 실패 → 저장 생략: {}({}) [{}]",
                    food.getFoodName(), food.getFoodCode(), getValidationErrors(food));
            return FoodSaveStatus.INVALID;
        }

        // 2. 중복 검사 (DB 조회)
        if (isDuplicate(food)) {
            log.warn("🚫 중복 → 저장 생략: {}({})",
                    food.getFoodName(), food.getFoodCode());
            return FoodSaveStatus.DUPLICATE;
        }

        // 3. 칼로리 누락 여부 확인 (저장은 진행, 경고 로그만 출력)
        if (food.getCalories() == null) {
            log.warn("⚠️ 칼로리 : Null → 저장 생략: {}({})",
                    food.getFoodName(), food.getFoodCode());
        }

        // 4. 저장 시도
        try {
            applyInsertDefaults(food);
            foodMapper.insertFood(food);
            log.info("✅ 저장 성공:  {}({})",
                    food.getFoodName(), food.getFoodCode());
            return FoodSaveStatus.SUCCESS;
        } catch (Exception e) {
            log.error("💥 저장 실패 → 저장 생략: {}({})",
                    food.getFoodName(), food.getFoodCode(), e);
            return FoodSaveStatus.ERROR;
        }
    }

    /**
     * 여러 음식 정보를 DB에 저장합니다.
     * - saveFood()를 호출하여 상태별 집계 처리
     *
     * @param foods 저장할 FoodDto 리스트
     * @return 저장 결과(성공, 중복, 실패 개수)
     */
    public FoodSaveResult saveFoods(List<FoodDto> foods) {
        int saved = 0, duplicated = 0, failed = 0;

        for (FoodDto food : foods) {
            FoodSaveStatus status = saveFood(food);
            switch (status) {
                case SUCCESS:
                    saved++;
                    break;
                case DUPLICATE:
                    duplicated++;
                    break;
                case INVALID:
                case ERROR:
                    failed++;
                    break;
            }
        }

        log.info("📊 저장 결과 → 성공: {}건 | 중복: {}건 | 실패: {}건", saved, duplicated, failed);
        return new FoodSaveResult(saved, duplicated, failed);
    }


    // ========================================================
    // [3] API → DB (Import 기능)  - 배치에 필요한 관리자 로그인 기능, resume 기능, 비동기 기능은 구현X
    // ========================================================
    /**
     * ✅ 키워드 리스트 기반 데이터 수집 & DB 저장
     *
     * @param keywords 수집할 키워드 리스트 (예: ["밥", "라면"])
     * @return 요약 문자열
     */
    public String importByKeywords(List<String> keywords) {
        int totalSaved = 0, totalRequests = 0, totalReceived = 0;
        int saved = 0, duplicated = 0, failed = 0;

        for (String keyword : keywords) {
            int totalCount = getTotalCount(keyword);  // 키워드별 전체 데이터 건수 조회
            log.info("─────────────────────────────────────────────────────────────────────────────────────");
            log.info("✅ [{}] 전체 데이터 건수: {} 건 - 데이터 수집 시작...", keyword, totalCount);

            for (int page = 1; page <= MAX_PAGES_PER_KEYWORD; page++) {
                log.info("======================================================================================");
                log.info("✅ [{}] - {}페이지 데이터 수집 시작...", keyword, page);

                if (totalSaved >= MAX_RECORDS_PER_RUN || totalRequests >= MAX_API_REQUESTS_PER_DAY) {
                    log.warn("⛔ 요청 한도 도달 → 저장: {}, 요청: {}", totalSaved, totalRequests);
                    break;
                }

                List<FoodDto> list = fetchPage(keyword, page);
                totalRequests++;

                if (list.isEmpty()) {
                    if (page == 1) {
                        log.info("📭 API 서버에 '{}' 키워드 데이터 없음", keyword);
                    } else {
                        log.info("📭 '{}' 키워드 → {}페이지에 더 이상 데이터 없음 (수집 종료)", keyword, page);
                    }
                    break;
                }

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
                    return "❌ 작업 중단 (Interrupted)";
                }
            }
        }

        String summary = String.format("""
            ✅ 키워드 기반 수집 완료
            ─────────────────────────────
            - 총 응답 항목 수:   %,d
            - 저장 성공:       %,d
            - 중복 제외:       %,d
            - 저장 실패:       %,d
            - 요청 횟수:       %,d
            """, totalReceived, saved, duplicated, failed, totalRequests);

        log.info(summary);
        return summary;
    }

// ========================================================
// [4] 유틸리티 메서드
// ========================================================

// --------------------------------------------------------
// [4-1] 내부 유틸 (API 호출/파싱 전용)
//     → FoodApiService에서만 사용
// --------------------------------------------------------
    /**
     * API 호출 후 items.item 배열 반환
     *
     * @param keyword 검색어
     * @param page    페이지 번호
     * @param size    페이지당 항목 수
     * @return JsonNode (item 배열)
     */
    private JsonNode callApi(String keyword, int page, int size) throws Exception {
        String url = buildApiUrl(keyword, page, size);
        log.info("🌐 API 요청 URL: {}", url);
//        log.info("🌐 API 요청 URL: {} (API Key Hidden)", url.replaceAll("serviceKey=[^&]+", "serviceKey=****"));

        String json = restTemplate.getForObject(url, String.class);

        if (json != null && json.trim().startsWith("<")) {
            throw new RuntimeException("❌ API 인증 실패 또는 ServiceKey 오류");
        }

        return objectMapper.readTree(json).path("body"); // body만 반환
    }

    /**
     * API URL 생성
     *
     * @param keyword 검색어
     * @param page 페이지 번호
     * @param size 페이지당 항목 수
     * @return API 요청 URL
     */
    private String buildApiUrl(String keyword, int page, int size) {
        String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
        return new StringBuilder(BASE_URL)
                .append("?serviceKey=").append(serviceKey)
                .append("&type=json")
                .append("&pageNo=").append(page)
                .append("&numOfRows=").append(size)
                .append("&FOOD_NM_KR=").append(encodedKeyword)
                .toString();
    }

    /**
     * API 응답 JSON → FoodDto 리스트 변환
     *
     * @param items JsonNode 배열
     * @return 변환된 FoodDto 리스트
     */
    private List<FoodDto> parseApiItems(JsonNode items) {
        List<FoodDto> result = new ArrayList<>();
        if (items.isArray()) {
            for (JsonNode item : items) {
                try {
                    FoodDto food = objectMapper.treeToValue(item, FoodDto.class);
                    UnitMappingUtils.applyParsedValue(food.getFoodServingSizeRaw(),
                            food::setFoodServingSizeValue, food::setFoodServingUnitId);
                    UnitMappingUtils.applyParsedValue(food.getFoodWeightRaw(),
                            food::setFoodWeightValue, food::setFoodWeightUnitId);
                    result.add(food);
                } catch (Exception e) {
                    log.warn("❌ DTO 변환 실패 → 건너뜀: {}", item.toPrettyString());
                }
            }
        }
        return result;
    }


// --------------------------------------------------------
// [4-2] 외부 유틸 후보 (다른 서비스에서도 쓸 가능성 있음)
//      → 나중에 FoodUtils 또는 Validator로 분리 고려
// --------------------------------------------------------
    /**
     * DB 중복 여부 체크
     */
    private boolean isDuplicate(FoodDto food) {
        return food.getFoodCode() != null && !food.getFoodCode().isBlank()
                && foodMapper.existsByFoodCode(food.getFoodCode()) > 0;
    }

    /**
     * 기본값 세팅 (생성일, 수정일 등)
     */
    // private static final String DEFAULT_DATA_SOURCE = "식품영양성분DB";

    private void applyInsertDefaults(FoodDto food) {
        if (food.getIsUserCreated() == null) food.setIsUserCreated("N");
        if (food.getIsImported() == null) food.setIsImported("N");
        if (food.getCreatedDate() == null) food.setCreatedDate(new Date());
        if (food.getUpdatedDate() == null) food.setUpdatedDate(new Date());
        if (food.getDataSourceName() == null) food.setDataSourceName(DEFAULT_DATA_SOURCE);
    }

// ========================================================
// [5] 유효성 검사
//     → FoodApiService에서만 사용
// ========================================================
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
        if (food.getFoodServingUnitId() == null || food.getFoodServingUnitId() < 0) {
            errors.add("Serving 단위 ID 없음");
        }
        if (food.getFoodWeightUnitId() == null || food.getFoodWeightUnitId() < 0) {
            errors.add("무게 단위 ID가 유효하지 않음");
        }

        return errors;
    }
}

