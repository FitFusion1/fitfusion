package com.fitfusion.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitfusion.dto.FoodDto;
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

    private static final String BASE_URL = "https://apis.data.go.kr/1471000/FoodNtrCpntDbInfo02/getFoodNtrCpntDbInq02";

    public void importFood(FoodDto foodDto) {
        applyInsertDefaults(foodDto);
        try {
            foodMapper.insertFood(foodDto);
            log.info("✅ 단일 INSERT 성공: {}", foodDto.getFoodCode());
        } catch (DuplicateKeyException e) {
            log.warn("🚫 단일 INSERT 중복 생략: {}", foodDto.getFoodCode());
        } catch (Exception e) {
            log.error("💥 단일 INSERT 실패: {}", foodDto, e);
        }
    }

    private void applyInsertDefaults(FoodDto dto) {
        if (dto.getIsUserCreated() == null) dto.setIsUserCreated("N");
        if (dto.getIsImported() == null) dto.setIsImported("N");
        if (dto.getCreatedDate() == null) dto.setCreatedDate(new Date());
        if (dto.getUpdatedDate() == null) dto.setUpdatedDate(new Date());
        if (dto.getDataSourceName() == null) dto.setDataSourceName("식품영양성분DB");
    }

    public int importFoodsAndSave(String keyword) {
        List<FoodDto> list = searchFood(keyword);
        int insertCount = 0;

        for (FoodDto foodDto : list) {

            // 필수값 누락 시 저장 스킵
            if (foodDto.getFoodName() == null || foodDto.getFoodName().isBlank()) {
                log.warn("❌ 식품명 누락 → 저장 생략: {}", foodDto);
                log.warn("❌ 식품명이 누락된 FOOD_CODE: {}", foodDto.getFoodCode());
                continue;
            }

            // 단위 ID 유효성 검사 (NOT NULL 제약 조건)
            if (foodDto.getFoodServingUnitId() == null || foodDto.getFoodServingUnitId() == -1) {
                log.warn("❌ Serving 단위 ID가 유효하지 않아 저장 생략: raw='{}'", foodDto.getFoodServingSizeRaw());
                log.warn("❌ Serving 단위 ID가 유효하지 않은 FOOD_CODE: {}", foodDto.getFoodCode());
                continue;
            }
            if (foodDto.getFoodWeightUnitId() == null || foodDto.getFoodWeightUnitId() == -1) {
                log.warn("❌ Weight 단위 ID가 유효하지 않아 저장 생략: raw='{}'", foodDto.getFoodWeightRaw());
                log.warn("❌ Weight 단위 ID가 유효하지 않은: {}", foodDto.getFoodCode());
                continue;
            }

            try {

                // ✅ 중복 검사 후 삽입 (FOOD_CODE 기준)
                if (foodDto.getFoodCode() != null && !foodDto.getFoodCode().isBlank()
                        && foodMapper.existsByFoodCode(foodDto.getFoodCode()) == 0) {

                    applyInsertDefaults(foodDto);  // 기본값 세팅
                    foodMapper.insertFood(foodDto); // 중복 시 예외 발생
                    insertCount++;
                    log.info("✅ INSERT 성공: {}", foodDto.getFoodCode());

                } else {
                    log.warn("🚫 중복으로 INSERT 생략: {}", foodDto.getFoodCode());
                }

            }catch (Exception e) {
                log.error("💥 INSERT 중 예외 발생: {}", foodDto, e);
            }
        }

        log.info("📥 공공데이터 API 호출 결과: 총 {}건", list.size());
        log.info("✅ 이 중 DB 저장 완료: {}건 (검색어: {})", insertCount, keyword);
        return insertCount;

    }

    /**
     * 공공데이터 API에서 데이터를 조회한다.
     *
     * @param foodName 검색어
     * @return 조회된 FoodDto 목록
     */
    public List<FoodDto> searchFood(String foodName) {
        try {
            String encodedKeyword = URLEncoder.encode(foodName, StandardCharsets.UTF_8);
            String url = BASE_URL +
                    "?serviceKey=" + serviceKey +
                    "&type=json&pageNo=1&numOfRows=5&FOOD_NM_KR=" + encodedKeyword;

            RestTemplate restTemplate = new RestTemplate();
            String json = restTemplate.getForObject(url, String.class);

            if (json != null && json.trim().startsWith("<")) {
                throw new RuntimeException("공공데이터 API 인증 실패 또는 서비스키 오류");
            }

            // JSON 파싱
            ObjectMapper mapper = new ObjectMapper();
            JsonNode items = mapper.readTree(json).path("body").path("items");

            List<FoodDto> list = new ArrayList<>();

            if (items.isArray()) {
                for (JsonNode item : items) {
                    try {

                        FoodDto dto = mapper.treeToValue(item, FoodDto.class); //JSON 노드 → DTO 매핑

                        // 단위 파싱
                        UnitMappingUtils.applyParsedValue(dto.getFoodServingSizeRaw(),
                                dto::setFoodServingSizeValue, dto::setFoodServingUnitId);
                        UnitMappingUtils.applyParsedValue(dto.getFoodWeightRaw(),
                                dto::setFoodWeightValue, dto::setFoodWeightUnitId);

                        list.add(dto); // DTO 변환 결과

                    } catch (Exception e) {
                        log.warn("❌ DTO 변환 실패 → 건너뜀: {}", item.toPrettyString());
                    }
                }
            } else {
                log.warn("❗응답 JSON의 'items'가 배열이 아닙니다. items = {}", items);
            }

            log.info("📊 API에서 가져온 유효한 데이터 개수 = {}", list.size());
            return list;

        } catch (Exception e) {
            log.error("💥 공공데이터 API 호출 실패", e);
            throw new RuntimeException("공공데이터 API 호출 실패", e);

        }

    }

    /**
     * DB 전체 저장용 (빈 검색어)
     */
    public int importAllFoods() {
        return importFoodsAndSave("");
    }

    /**
     * 특정 키워드로 API 검색 후 개수 반환
     *
     * @param keyword 검색어
     * @return 개수
     */
    public int getTotalCount(String keyword) {
        List<FoodDto> list = searchFood(keyword);
        return list.size();
    }

/**
 *  다듬으면 관리자 페이지에서 API -> DB저장 기능으로 쓸 수 있을듯
 *  * 중복 코드 많고, 기능에 대한 고민 필요함.
 *
 *  * 식품의약품안전처_식품영양성분DB정보
 *    - https://www.data.go.kr/data/15127578/openapi.do
 *
 *  * API 제약사항
 *    - 총 데이터 약 16만 건
 *    - 요청 1회당 최대 100건
 *    - 일일 최대 10,000건 호출 가능
 *
 *  * 저장 전략 (2단계)
 *    [1순위] 자주 소비되는 음식 우선 수집
 *        예) "밥", "라면", "닭가슴살"
 *
 *    [2순위] 그 외 전체 음식 수집 (조건 없이 FOOD_CD 오름차순)
 *        → 중복은 DB 저장 시 생략
 *
 *  * 효율적 수집 및 중복 방지를 위해 FOOD_CODE 기준 중복 체크 필수
 */
// ✅ FoodApiService.java 내부
public void importPrioritizedThenRemaining(List<String> priorityKeywords) {
    log.info("🚀 [1단계] 키워드 우선 수집 시작 ({}건)", priorityKeywords.size());
    for (String keyword : priorityKeywords) {
        log.info("🔎 키워드 수집 중: '{}'", keyword);
        for (int page = 1; page <= 1000; page++) {
            List<FoodDto> items = searchFood(keyword, page, 100);
            if (items.isEmpty()) {
                log.info("📭 키워드 '{}' page {}: 결과 없음 → 종료", keyword, page);
                break;
            }

            saveFoods(items);
        }
    }

    log.info("🚀 [2단계] 전체 순회 수집 시작 (food_cd 오름차순)");
    for (int page = 1; page <= 2000; page++) {
        List<FoodDto> items = searchFood("", page, 100); // 조건 없이 → 전체 수집
        if (items.isEmpty()) {
            log.info("📭 전체 수집 page {}: 결과 없음 → 종료", page);
            break;
        }

        saveFoods(items); // 중복 검사 포함
    }

    log.info("✅ 전체 import 완료");
}

    public void saveFoods(List<FoodDto> list) {
        for (FoodDto dto : list) {
            try {
                if (dto.getFoodCode() != null && !dto.getFoodCode().isBlank()
                        && foodMapper.existsByFoodCode(dto.getFoodCode()) == 0) {
                    applyInsertDefaults(dto);
                    foodMapper.insertFood(dto);
                    log.info("✅ INSERT 성공: {}", dto.getFoodCode());
                } else {
                    log.debug("🚫 중복 생략: {}", dto.getFoodCode());
                }
            } catch (Exception e) {
                log.error("💥 INSERT 예외 발생: {}", dto, e);
            }
        }
    }

    public List<FoodDto> searchFood(String keyword, int page, int size) {
        try {
            String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
            String url = BASE_URL +
                    "?serviceKey=" + serviceKey +
                    "&type=json&pageNo=" + page +
                    "&numOfRows=" + size +
                    "&FOOD_NM_KR=" + encodedKeyword;

            RestTemplate restTemplate = new RestTemplate();
            String json = restTemplate.getForObject(url, String.class);

            if (json != null && json.trim().startsWith("<")) {
                throw new RuntimeException("공공데이터 API 인증 실패 또는 서비스키 오류");
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode items = mapper.readTree(json).path("body").path("items");
            List<FoodDto> list = new ArrayList<>();

            if (items.isArray()) {
                for (JsonNode item : items) {
                    try {
                        FoodDto dto = mapper.treeToValue(item, FoodDto.class);
                        UnitMappingUtils.applyParsedValue(dto.getFoodServingSizeRaw(),
                                dto::setFoodServingSizeValue, dto::setFoodServingUnitId);
                        UnitMappingUtils.applyParsedValue(dto.getFoodWeightRaw(),
                                dto::setFoodWeightValue, dto::setFoodWeightUnitId);
                        list.add(dto);
                    } catch (Exception e) {
                        log.warn("❌ DTO 변환 실패 → 건너뜀: {}", item.toPrettyString());
                    }
                }
            }
            return list;
        } catch (Exception e) {
            log.error("💥 API 호출 실패", e);
            return List.of();
        }
    }

}
