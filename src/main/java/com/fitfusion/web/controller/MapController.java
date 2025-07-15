package com.fitfusion.web.controller;

import com.fitfusion.dto.DetailDataDto;
import com.fitfusion.dto.GymDataDto;
import com.fitfusion.dto.KakaoplaceDto;
import com.fitfusion.service.KakaoGymSearchService;
import com.fitfusion.service.KakaoShowGymData;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MapController {

    private final KakaoGymSearchService kakaoGymSearchService;
    private final KakaoShowGymData kakaoShowGymData;

    @GetMapping("/api/kakao/gyms")
    public List<KakaoplaceDto> UserNearByGyms(@RequestParam("lat") Double latitude, @RequestParam("lon") Double longitude) {
        String keyword = "헬스장";
        return kakaoGymSearchService.searchGym(keyword, latitude, longitude);
    }

    @PostMapping("/api/gyms")
    public GymDataDto saveGym(@RequestBody GymDataDto gymDataDto) {

        kakaoShowGymData.insertGym(gymDataDto);
        System.out.println("gymDataDto: " + gymDataDto);
        System.out.println("kakaoPlaceId: " + gymDataDto.getKakaoPlaceId());
        return kakaoShowGymData.selectByKakaoPlaceId(gymDataDto.getKakaoPlaceId());
    }
}
