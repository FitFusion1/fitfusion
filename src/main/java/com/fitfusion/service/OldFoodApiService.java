//package com.fitfusion.service;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fitfusion.dto.FoodDto;
//import com.fitfusion.mapper.FoodMapper;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.net.URLEncoder;
//import java.nio.charset.StandardCharsets;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class FoodApiService {
//
//    private final FoodMapper foodMapper;
//
//    public int importFoodsAndSave(String keyword) {
//        List<FoodDto> list = searchFood(keyword);
//
//        int insertCount = 0;
//        for (FoodDto dto : list) {
//            try {
//                int result = foodMapper.insertFood(dto);
//                insertCount += result;
//                log.debug("Inserted: {}", dto.getFoodName());
//            } catch (Exception e) {
//                log.error("Insert error: {}", dto, e);
//            }
//        }
//        log.info("총 {}개의 데이터를 저장했습니다. (검색어: {})", insertCount, keyword);
//        return insertCount;
//    }
//
//    public List<FoodDto> searchFood(String foodName) {
//        try {
//            String serviceKey = "09J9RfG3PEw4tLqCW/Px5eZjpoXzwT7Ojcd6j3LRmcD6qKCJOgyOlcoNmVi4lApSzuN4kRYsCKt8U0UZRV8mzQ==";
//            String encodedKeyword = URLEncoder.encode(foodName, StandardCharsets.UTF_8);
//
//            String url = "https://apis.data.go.kr/1471000/FoodNtrCpntDbInfo02/getFoodNtrCpntDbInq02"
//                    + "?serviceKey=" + serviceKey
//                    + "&type=json"
//                    + "&pageNo=1"
//                    + "&numOfRows=5"
//                    + "&FOOD_NM_KR=" + encodedKeyword;
//
//            log.info("공공데이터 API 호출 URL = {}", url);
//
//            RestTemplate restTemplate = new RestTemplate();
//            String json = restTemplate.getForObject(url, String.class);
//            log.info("API 원문 JSON = {}", json);
//
//            if (json != null && json.trim().startsWith("<")) {
//                log.error("공공데이터 API에서 XML 에러 응답 수신: {}", json);
//                throw new RuntimeException("공공데이터 API 인증 실패 또는 서비스키 오류");
//            }
//
//            ObjectMapper mapper = new ObjectMapper();
//            JsonNode root = mapper.readTree(json);
//
//            // ✅ root 전체 찍기
//            log.info("root = {}", root.toPrettyString());
//
//            JsonNode items = root.path("body").path("items");
//
//            log.info("items = {}", items.toPrettyString());
//
//            List<FoodDto> list = new ArrayList<>();
//
//            if (items.isArray()) {
//                for (JsonNode item : items) {
//                    FoodDto dto = mapToFoodDto(item);
//                    log.info("DTO 변환 결과 = {}", dto);
//                    list.add(dto);
//                }
//            } else {
//                log.warn("items가 배열이 아닙니다. items={}", items);
//            }
//
//            log.info("API에서 가져온 데이터 개수 = {}", list.size());
//            return list;
//
//        } catch (Exception e) {
//            log.error("공공데이터 API 호출 실패", e);
//            throw new RuntimeException("공공데이터 API 호출 실패", e);
//        }
//    }
//
//    private FoodDto mapToFoodDto(JsonNode item) {
//        FoodDto dto = new FoodDto();
//
//        dto.setFoodName(item.path("FOOD_NM_KR").asText(""));
//        dto.setGroupName(item.path("FOOD_CAT1_NM").asText(""));
//        dto.setFoodCategoryCode(item.path("FOOD_CAT1_CD").asText(""));
//        dto.setFoodCategoryName(item.path("FOOD_CAT1_NM").asText(""));
//        dto.setFoodMidCategoryCode(item.path("FOOD_CAT2_CD").asText(""));
//        dto.setFoodMidCategoryName(item.path("FOOD_CAT2_NM").asText(""));
//        dto.setFoodSmallCategoryCode(item.path("FOOD_CAT3_CD").asText(""));
//        dto.setFoodSmallCategoryName(item.path("FOOD_CAT3_NM").asText(""));
//        dto.setFoodDetailCategoryCode(item.path("FOOD_CAT4_CD").asText(""));
//        dto.setFoodDetailCategoryName(item.path("FOOD_CAT4_NM").asText(""));
//        dto.setServingSizeRaw(item.path("SERVING_SIZE").asText(""));
//        dto.setServingSize(parseNumber(item.path("SERVING_SIZE").asText()));
//        dto.setFoodWeight(parseNumber(item.path("Z10500").asText()));
//        dto.setReferenceIntake(parseNumber(item.path("NUTRI_AMOUNT_SERVING").asText()));
//        dto.setCalories(parseNumber(item.path("AMT_NUM1").asText()));
//        dto.setCarbohydrateG(parseNumber(item.path("AMT_NUM2").asText()));
//        dto.setProteinG(parseNumber(item.path("AMT_NUM3").asText()));
//        dto.setFatG(parseNumber(item.path("AMT_NUM4").asText()));
//        dto.setSugarG(parseNumber(item.path("AMT_NUM5").asText()));
//        dto.setSodiumMg(parseNumber(item.path("AMT_NUM6").asText()));
//        dto.setCholesterolMg(parseNumber(item.path("AMT_NUM7").asText()));
//        dto.setSaturatedFatG(parseNumber(item.path("AMT_NUM8").asText()));
//        dto.setTransFatG(parseNumber(item.path("AMT_NUM9").asText()));
//        dto.setPotassiumMg(parseNumber(item.path("AMT_NUM11").asText()));
//        dto.setFiberG(parseNumber(item.path("AMT_NUM12").asText()));
//        dto.setIronMg(parseNumber(item.path("AMT_NUM13").asText()));
//        dto.setCalciumMg(parseNumber(item.path("AMT_NUM14").asText()));
//        dto.setVitaminCMg(parseNumber(item.path("AMT_NUM15").asText()));
//        dto.setSugarAlcoholG(parseNumber(item.path("AMT_NUM16").asText()));
//        dto.setCaffeineMg(parseNumber(item.path("AMT_NUM17").asText()));
//        dto.setAlcoholG(parseNumber(item.path("AMT_NUM18").asText()));
//        dto.setDataSourceName(item.path("SUB_REF_NAME").asText(""));
//        dto.setDataCreatedDate(parseDate(item.path("RESEARCH_YMD").asText("")));
//        dto.setDataModifiedDate(parseDate(item.path("UPDATE_DATE").asText("")));
//        dto.setDataType(item.path("DB_CLASS_NM").asText(""));
//        dto.setMakerName(item.path("MAKER_NM").asText(""));
//        dto.setImporterName(item.path("IMP_MANUFAC_NM").asText(""));
//        dto.setDistributorName(item.path("SELLER_MANUFAC_NM").asText(""));
//        dto.setOriginCountryName(item.path("NATION_NM").asText(""));
//        dto.setIsImported(item.path("IMP_YN").asText("N"));
//        dto.setImageUrl("");
//        dto.setIsUserCreated("N");
//        dto.setCreatedByUserId(null);
//        dto.setCreatedDate(null);
//
//        return dto;
//    }
//
//    private Double parseNumber(String str) {
//        if (str == null || str.isBlank()) return 0.0;
//        try {
//            return Double.parseDouble(str.replace(",", ""));
//        } catch (NumberFormatException e) {
//            return 0.0;
//        }
//    }
//
//    private Date parseDate(String str) {
//        if (str == null || str.isBlank()) return null;
//        try {
//            if (str.matches("\\d{4}-\\d{2}-\\d{2}")) {
//                return new SimpleDateFormat("yyyy-MM-dd").parse(str);
//            } else if (str.matches("\\d{8}")) {
//                return new SimpleDateFormat("yyyyMMdd").parse(str);
//            } else {
//                return null;
//            }
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    public int importAllFoods() {
//        return importFoodsAndSave("");
//    }
//
//    public int getTotalCount(String keyword) {
//        List<FoodDto> list = searchFood(keyword);
//        return list.size();
//    }
//}
