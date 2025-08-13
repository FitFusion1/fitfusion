package com.fitfusion.service;

import lombok.Getter;
import lombok.Setter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;


@Service
public class KakaoLocalService {

    @Autowired
    private RestTemplate restTemplate;

    private static final String kakaoApiUrl = "https://dapi.kakao.com/v2/local/search/keyword.json";

    @Value("${kakao.rest-api-key}")
    private String restApiKey;

    public String searchPlace(String keyword, Double latitude, Double longitude)  {


        // 1. URL 만들기
        String requestUrl = kakaoApiUrl + "?query=" + keyword +
                "&x=" + longitude + "&y=" + latitude + "&radius=2000";

        //2 요청 헤더 만들기
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + restApiKey);
        // 3. HttpEntity 만들기 (헤더 포함)
        HttpEntity<String> entity = new HttpEntity<>(headers);


        ResponseEntity<String> response = restTemplate.exchange(
                requestUrl,
                HttpMethod.GET,
                entity,
                String.class
        );

        return response.getBody();
    }

}
