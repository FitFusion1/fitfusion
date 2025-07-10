package com.fitfusion.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class OpenAiTranslationService {

    @Value("${openai.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String translateToKorean(String text) {
        if (text == null || text.isBlank()) return "";

        // HTML 태그 제거
        String cleanText = Jsoup.parse(text).text();

        String url = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "model", "gpt-3.5-turbo",
                "messages", List.of(
                        Map.of("role", "system", "content", "운동 설명 텍스트를 자연스러운 한국어로 번역해주세요. 너무 딱딱하지 않게 자연스럽게 말하듯이 번역해주세요."),
                        Map.of("role", "user", "content", cleanText)
                )
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            JsonNode json = objectMapper.readTree(response.getBody());
            return json.at("/choices/0/message/content").asText().trim();
        } catch (Exception e) {
            return "(번역 실패: " + cleanText + ")";
        }
    }
}
