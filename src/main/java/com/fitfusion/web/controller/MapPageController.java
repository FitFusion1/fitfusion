package com.fitfusion.web.controller;

import com.fitfusion.dto.DetailDataDto;
import com.fitfusion.dto.GymReviewDto;
import com.fitfusion.security.SecurityUser;
import com.fitfusion.service.KakaoShowGymData;
import com.fitfusion.vo.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MapPageController {

    private final KakaoShowGymData kakaoShowGymData;

    // 기본 경로 접근
    @GetMapping("/map")
    public String map(Model model, @AuthenticationPrincipal SecurityUser securityUser) {
        if (securityUser != null ) {
            model.addAttribute("loginUserId", securityUser.getUser().getUserId());
        } else {
            model.addAttribute("loginUserId", null);
        }
        return "healthGym/map";
    }

    // 기본 경로 접근
    @GetMapping("/gyms/detail/{gymId}")
    public String detail(@PathVariable int gymId, Model model, @AuthenticationPrincipal SecurityUser securityUser) {

        DetailDataDto detailData = kakaoShowGymData.detailForm(gymId);

        if (securityUser != null) {
            User user = securityUser.getUser();
            model.addAttribute("user", user);
            System.out.println("userName: " + user.getName());
            model.addAttribute("loginUserId", user.getUserId());
            System.out.println("loginUserId: " + user.getUserId());
        } else {
            model.addAttribute("loginUserId", null);
        }
        model.addAttribute("detailData", detailData);
        return "healthGym/detail";
    }


    @GetMapping("/gyms/compare")
    public String showComparePage(@RequestParam("gymId") List<Integer> gymIds, Model model, @AuthenticationPrincipal SecurityUser securityUser) {

        List<DetailDataDto> gymList = kakaoShowGymData.detailFormList(gymIds);

        if (securityUser != null) {
            model.addAttribute("loginUserId", securityUser.getUser().getUserId());
        } else {
            model.addAttribute("loginUserId", null);
        }
        model.addAttribute("detailDataList", gymList);

        return "healthGym/recentGyms";
    }
}
