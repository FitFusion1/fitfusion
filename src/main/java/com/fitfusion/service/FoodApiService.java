package com.fitfusion.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitfusion.dto.FoodDto;
import com.fitfusion.mapper.FoodMapper;
import com.fitfusion.util.UnitMappingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
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
    private final JdbcTemplate jdbcTemplate;

    private static final String SERVICE_KEY = "09J9RfG3PEw4tLqCW/Px5eZjpoXzwT7Ojcd6j3LRmcD6qKCJOgyOlcoNmVi4lApSzuN4kRYsCKt8U0UZRV8mzQ==";
    private static final String BASE_URL = "https://apis.data.go.kr/1471000/FoodNtrCpntDbInfo02/getFoodNtrCpntDbInq02";

    public void importFood(FoodDto foodDto) {
        applyInsertDefaults(foodDto);
        foodMapper.insertFood(foodDto);
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
                continue;
            }

            // 단위 ID 유효성 검사 (NOT NULL 제약 조건)
            if (foodDto.getFoodServingUnitId() == null || foodDto.getFoodServingUnitId() == -1) {
                log.warn("❌ Serving 단위 ID가 유효하지 않아 저장 생략: raw='{}'", foodDto.getFoodServingSizeRaw());
                continue;
            }
            if (foodDto.getFoodWeightUnitId() == null || foodDto.getFoodWeightUnitId() == -1) {
                log.warn("❌ Weight 단위 ID가 유효하지 않아 저장 생략: raw='{}'", foodDto.getFoodWeightRaw());
                continue;
            }

            try {
                applyInsertDefaults(foodDto);  // 기본값 세팅
                foodMapper.insertFood(foodDto); // 중복 시 예외 발생
                insertCount++;
                log.info("✅ INSERT 성공: {}", foodDto.getFoodCode());

            } catch (DuplicateKeyException e) {
                log.warn("🚫 중복으로 INSERT 생략: {}", foodDto.getFoodCode());
            } catch (Exception e) {
                log.error("💥 INSERT 중 예외 발생: {}", foodDto, e);
            }
        }

        log.info("총 {}개의 데이터를 저장했습니다. (검색어: {})", insertCount, keyword);
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
                    "?serviceKey=" + SERVICE_KEY +
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

}
