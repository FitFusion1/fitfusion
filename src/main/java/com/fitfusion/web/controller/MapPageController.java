package com.fitfusion.web.controller;

import com.fitfusion.dto.DetailDataDto;
import com.fitfusion.service.KakaoShowGymData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class MapPageController {

    private final KakaoShowGymData kakaoShowGymData;

    // 기본 경로 접근
    @GetMapping("/map")
    public String map() {
        return "map";
    }

    // 기본 경로 접근
    @GetMapping("/gyms/detail/{gymId}")
    public String detail(@PathVariable int gymId, Model model) {

        DetailDataDto detailData = kakaoShowGymData.detailForm(gymId);
        System.out.println("gymId: " + gymId);
        System.out.println("detailData: " + detailData);
        model.addAttribute("detailData", detailData);
        return "detail";
    }


    @GetMapping("/compare")
    public String showComparePage() {
        return "recentGyms";
    }
}
