package com.fitfusion.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitfusion.dto.FoodDto;
import com.fitfusion.dto.FoodSaveResult;
import com.fitfusion.mapper.FoodMapper;
import com.fitfusion.util.UnitMappingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
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
public class FoodApiService {

    private final FoodMapper foodMapper;

    @Value("${mfds.fooddb.apiKey}")
    private String serviceKey;

    private static final String BASE_URL =
            "https://apis.data.go.kr/1471000/FoodNtrCpntDbInfo02/getFoodNtrCpntDbInq02";

    /**
     * 단일 API 호출 (검색어 + 페이지 + 건수)
     */
    public List<FoodDto> searchFood(String keyword, int page, int size) {
        try {
            String url = buildApiUrl(keyword, page, size);
            String json = new RestTemplate().getForObject(url, String.class);

            if (json != null && json.trim().startsWith("<")) {
                throw new RuntimeException("❌ 공공데이터 API 인증 실패 또는 서비스키 오류");
            }

            return parseApiResponse(json);

        } catch (Exception e) {
            log.error("💥 API 호출 실패: {}", e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * 단일 API 호출 (검색어만)
     */
    public List<FoodDto> searchFood(String keyword) {
        return searchFood(keyword, 1, 10);
    }

    /**
     * 단일 Food 저장 (유효성 + 중복 검사 포함)
     */
    public boolean saveFoodIfValid(FoodDto dto) {
        if (dto.getFoodName() == null || dto.getFoodName().isBlank()) {
            log.warn("❌ 식품명 누락 → 생략: {}", dto.getFoodCode());
            return false;
        }

        if (dto.getFoodServingUnitId() == null || dto.getFoodServingUnitId() < 0) {
            log.warn("❌ Serving 단위 ID 없음 → 생략: {}", dto.getFoodCode());
            return false;
        }

        if (dto.getFoodWeightUnitId() == null || dto.getFoodWeightUnitId() < 0) {
            log.warn("❌ Weight 단위 ID 없음 → 생략: {}", dto.getFoodCode());
            return false;
        }

        try {
            if (dto.getFoodCode() != null && !dto.getFoodCode().isBlank()
                    && foodMapper.existsByFoodCode(dto.getFoodCode()) == 0) {

                applyInsertDefaults(dto);
                foodMapper.insertFood(dto);
                log.info("✅ INSERT 성공: {}", dto.getFoodCode());
                return true;

            } else {
                log.debug("🚫 중복 생략: {}", dto.getFoodCode());
            }
        } catch (Exception e) {
            log.error("💥 INSERT 실패: {}", dto, e);
        }
        return false;
    }

    /**
     * 여러 DTO 저장 (saveFoodIfValid 반복 호출)
     */
//    public void saveFoods(List<FoodDto> list) {
//        for (FoodDto dto : list) {
//            saveFoodIfValid(dto);
//        }
//    }
    public FoodSaveResult saveFoods(List<FoodDto> foods) {
        int saved = 0;
        int duplicated = 0;
        int failed = 0;

        for (FoodDto food : foods) {
            try {
                applyInsertDefaults(food); // 기본값 설정
                foodMapper.insertFood(food);
                saved++;
            } catch (DuplicateKeyException e) {
                duplicated++;
                log.warn("⚠️ 중복: {}", food.getFoodName());
            } catch (Exception e) {
                failed++;
                log.error("❌ 저장 실패: {}", food.getFoodName(), e);
            }
        }

        log.info("📊 저장 결과 → 성공: {}건 | 중복: {}건 | 실패: {}건", saved, duplicated, failed);
        log.info("─────────────────────────────────────────────────────────"); // 혹은 System.out.println();
        return new FoodSaveResult(saved, duplicated, failed);
    }

    /**
     * 단건 수동 저장 (관리자 입력용)
     */
    public void importFood(FoodDto dto) {
        applyInsertDefaults(dto);
        try {
            foodMapper.insertFood(dto);
            log.info("✅ 단건 INSERT 성공: {}", dto.getFoodCode());
        } catch (DuplicateKeyException e) {
            log.warn("🚫 중복 생략 (단건): {}", dto.getFoodCode());
        } catch (Exception e) {
            log.error("💥 단건 INSERT 실패: {}", dto, e);
        }
    }

    /**
     * API 검색 후 저장 (간이용)
     */
    public int importFoodsAndSave(String keyword) {
        List<FoodDto> list = searchFood(keyword);
        int inserted = 0;
        for (FoodDto dto : list) {
            if (saveFoodIfValid(dto)) inserted++;
        }
        log.info("📦 저장 완료: {}/{}건 (검색어: {})", inserted, list.size(), keyword);
        return inserted;
    }

    /**
     * 빈 키워드로 전체 저장
     */
    public int importAllFoods() {
        return importFoodsAndSave("");
    }

    /**
     * 키워드에 대한 총 건수
     */
    public int getTotalCount(String keyword) {
        return searchFood(keyword).size();
    }

    /**
     * 키워드 우선 수집 → 전체 순회 수집 (관리자용)
     */
    public void importPrioritizedThenRemaining(List<String> priorityKeywords) {
        log.info("🚀 [1단계] 우선 키워드 수집 시작");
        for (String keyword : priorityKeywords) {
            for (int page = 1; page <= 1000; page++) {
                List<FoodDto> list = searchFood(keyword, page, 100);
                if (list.isEmpty()) break;
                saveFoods(list);
            }
        }

        log.info("🚀 [2단계] 전체 수집 시작");
        for (int page = 1; page <= 2000; page++) {
            List<FoodDto> list = searchFood("", page, 100);
            if (list.isEmpty()) break;
            saveFoods(list);
        }

        log.info("✅ 전체 수집 완료");
    }

    // ================= 내부 유틸 ================= //

    private String buildApiUrl(String keyword, int page, int size) {
        String encoded = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
        return BASE_URL + "?serviceKey=" + serviceKey +
                "&type=json&pageNo=" + page + "&numOfRows=" + size +
                "&FOOD_NM_KR=" + encoded;
    }

    private List<FoodDto> parseApiResponse(String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode items = mapper.readTree(json).path("body").path("items");

        List<FoodDto> result = new ArrayList<>();
        if (items.isArray()) {
            for (JsonNode item : items) {
                try {
                    FoodDto dto = mapper.treeToValue(item, FoodDto.class);
                    UnitMappingUtils.applyParsedValue(dto.getFoodServingSizeRaw(),
                            dto::setFoodServingSizeValue, dto::setFoodServingUnitId);
                    UnitMappingUtils.applyParsedValue(dto.getFoodWeightRaw(),
                            dto::setFoodWeightValue, dto::setFoodWeightUnitId);
                    result.add(dto);
                } catch (Exception e) {
                    log.warn("❌ DTO 변환 실패 → 건너뜀: {}", item.toPrettyString());
                }
            }
        }
        return result;
    }

    private void applyInsertDefaults(FoodDto dto) {
        if (dto.getIsUserCreated() == null) dto.setIsUserCreated("N");
        if (dto.getIsImported() == null) dto.setIsImported("N");
        if (dto.getCreatedDate() == null) dto.setCreatedDate(new Date());
        if (dto.getUpdatedDate() == null) dto.setUpdatedDate(new Date());
        if (dto.getDataSourceName() == null) dto.setDataSourceName("식품영양성분DB");
    }
}
