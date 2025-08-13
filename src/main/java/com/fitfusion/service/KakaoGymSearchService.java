package com.fitfusion.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitfusion.dto.KakaoSearchResponseDto;
import com.fitfusion.dto.KakaoplaceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KakaoGymSearchService {

    private final KakaoLocalService kakaoLocalService;
    private final ObjectMapper objectMapper;

    public List<KakaoplaceDto> searchGym(String keyword, Double latitude, Double longitude) {

        String data = kakaoLocalService.searchPlace(keyword, latitude, longitude);

        System.out.println("인코딩 되기 전 keyword: " + keyword);

        KakaoSearchResponseDto responseDto;

        try {
         responseDto = objectMapper.readValue(data, KakaoSearchResponseDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("json 매핑 실패했습니다.", e);
        }

        return responseDto.getDocuments();
    }
}
