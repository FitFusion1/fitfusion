package com.fitfusion.web.controller;

import com.fitfusion.dto.DetailDataDto;
import com.fitfusion.security.SecurityUser;
import com.fitfusion.service.KakaoShowGymData;
import com.fitfusion.vo.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public String detail(@PathVariable int gymId, Model model, @AuthenticationPrincipal SecurityUser securityUser) {

        DetailDataDto detailData = kakaoShowGymData.detailForm(gymId);
        User user = securityUser.getUser();

        model.addAttribute("user", user);
        model.addAttribute("loginUserId", user.getUserId());
        model.addAttribute("detailData", detailData);
        return "detail";
    }


    @GetMapping("/compare")
    public String showComparePage() {
        return "recentGyms";
    }
}
