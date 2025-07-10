package com.fitfusion.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoLocalService {

    @Autowired
    private RestTemplate restTemplate;

    private static final String kakaoApiUrl = "https://dapi.kakao.com/v2/local/search/keyword.json";
    private static final String restApiKey = "258fb141165b1f58e8cb1847169c112f";

    public String searchPlace(String keyword) {
        // 1. URL 만들기
        String requestUrl = kakaoApiUrl + "?keyword=" + keyword;

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
