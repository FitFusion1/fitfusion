package com.fitfusion.web.controller;

import com.fitfusion.dto.KakaoSearchResponseDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MapController {

    // 카카오 api 정보를 json형태로 불러와서 위도 경도 정보를 넘겨줌
    @GetMapping("/api/kakao/gyms")
    public List<KakaoSearchResponseDto> userNearByGyms(@RequestParam("lat") Double latitude, @RequestParam("lon") Double longitude) {

        return null;
    }

}
