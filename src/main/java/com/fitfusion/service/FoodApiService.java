package com.fitfusion.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitfusion.dto.FoodDto;
import com.fitfusion.mapper.FoodMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FoodApiService {

    private final FoodMapper foodMapper;

    /**
     * 공공데이터 API에서 데이터를 조회하고 DB에 저장한다.
     *
     * @param keyword 검색어
     * @return 저장된 개수
     */
    public int importFoodsAndSave(String keyword) {
        List<FoodDto> list = searchFood(keyword);

        int insertCount = 0;
        for (FoodDto dto : list) {
            try {
                int result = foodMapper.insertFood(dto);
                insertCount += result;
                log.debug("Inserted: {}", dto.getFoodName());
            } catch (Exception e) {
                log.error("Insert error: {}", dto, e);
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
            String serviceKey = "09J9RfG3PEw4tLqCW/Px5eZjpoXzwT7Ojcd6j3LRmcD6qKCJOgyOlcoNmVi4lApSzuN4kRYsCKt8U0UZRV8mzQ==";
            String encodedKeyword = URLEncoder.encode(foodName, StandardCharsets.UTF_8);

            String url = "https://apis.data.go.kr/1471000/FoodNtrCpntDbInfo02/getFoodNtrCpntDbInq02"
                    + "?serviceKey=" + serviceKey
                    + "&type=json"
                    + "&pageNo=1"
                    + "&numOfRows=5"
                    + "&FOOD_NM_KR=" + encodedKeyword;

            log.info("공공데이터 API 호출 URL = {}", url);

            RestTemplate restTemplate = new RestTemplate();
            String json = restTemplate.getForObject(url, String.class);
            log.info("API 원문 JSON = {}", json);

            if (json != null && json.trim().startsWith("<")) {
                log.error("공공데이터 API에서 XML 에러 응답 수신: {}", json);
                throw new RuntimeException("공공데이터 API 인증 실패 또는 서비스키 오류");
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);
            JsonNode items = root.path("body").path("items");

            log.info("items = {}", items.toPrettyString());

            List<FoodDto> list = new ArrayList<>();

            if (items.isArray()) {
                for (JsonNode item : items) {
                    // treeToValue → FoodDto 변환
                    FoodDto dto = mapper.treeToValue(item, FoodDto.class);

                    // log 찍어보자
                    log.info("DTO 변환 결과 = {}", dto);

                    list.add(dto);
                }
            } else {
                log.warn("items가 배열이 아닙니다. items={}", items);
            }

            log.info("API에서 가져온 데이터 개수 = {}", list.size());
            return list;

        } catch (Exception e) {
            log.error("공공데이터 API 호출 실패", e);
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
