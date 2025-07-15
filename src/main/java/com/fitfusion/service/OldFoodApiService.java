//package com.fitfusion.service;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fitfusion.dto.FoodDto;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.util.UriComponentsBuilder;
//
//import java.net.URI;
//import java.net.URLEncoder;
//import java.nio.charset.StandardCharsets;
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//public class FoodApiService {
//
//    private final RestTemplate restTemplate;
//
//    public FoodApiService() {
//        this.restTemplate = new RestTemplate();
//    }
//
//    public List<FoodDto> searchFood(String foodName) {
//        String serviceKey = "09J9RfG3PEw4tLqCW%2FPx5eZjpoXzwT7Ojcd6j3LRmcD6qKCJOgyOlcoNmVi4lApSzuN4kRYsCKt8U0UZRV8mzQ%3D%3D";
//
//        URI uri = UriComponentsBuilder
//                .fromHttpUrl("https://apis.data.go.kr/1471000/FoodNtrCpntDbInfo02/getFoodNtrCpntDbInq02")
//                .queryParam("serviceKey", serviceKey)
//                .queryParam("type", "json")
//                .queryParam("pageNo", 1)
//                .queryParam("numOfRows", 5)
//                .queryParam("FOOD_NM_KR", URLEncoder.encode(foodName, StandardCharsets.UTF_8))
//                .build(true)
//                .toUri();
//
//        System.out.println("호출 URI : " + uri);
//
//        String json = restTemplate.getForObject(uri, String.class);
//        System.out.println("API 응답 JSON: " + json);
//
//        List<FoodDto> list = new ArrayList<>();
//
//        try {
//            ObjectMapper mapper = new ObjectMapper();
//            JsonNode root = mapper.readTree(json);
//            JsonNode items = root.at("/body/items");
//
//            if (items.isArray()) {
//                for (JsonNode item : items) {
//                    String name = item.path("FOOD_NM_KR").asText();
//                    String servingSize = item.path("SERVING_SIZE").asText();
//                    String kcal = item.path("NUTR_CONT1").asText();
//                    String carb = item.path("NUTR_CONT2").asText();
//                    String protein = item.path("NUTR_CONT3").asText();
//                    String fat = item.path("NUTR_CONT4").asText();
//                    String sugars = item.path("NUTR_CONT5").asText();
//                    String sodium = item.path("NUTR_CONT6").asText();
//                    String cholesterol = item.path("NUTR_CONT7").asText();
//                    String satFat = item.path("NUTR_CONT8").asText();
//                    String transFat = item.path("NUTR_CONT9").asText();
//
//                    FoodDto dto = new FoodDto(
//                            name,
//                            servingSize,
//                            kcal,
//                            carb,
//                            protein,
//                            fat,
//                            sugars,
//                            sodium,
//                            cholesterol,
//                            satFat,
//                            transFat
//                    );
//
//                    list.add(dto);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return list;
//    }
//}
