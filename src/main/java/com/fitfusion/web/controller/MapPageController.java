package com.fitfusion.web.controller;

import com.fitfusion.dto.DetailDataDto;
import com.fitfusion.dto.GymReviewDto;
import com.fitfusion.security.SecurityUser;
import com.fitfusion.service.KakaoShowGymData;
import com.fitfusion.vo.GymLikes;
import com.fitfusion.vo.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

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
    public String detail(@PathVariable int gymId,
                         @AuthenticationPrincipal SecurityUser securityUser,
                         Model model) {

        DetailDataDto detailData = kakaoShowGymData.detailForm(gymId);
        model.addAttribute("detailData", detailData);

        if (securityUser != null) {
            User user = securityUser.getUser();
            model.addAttribute("user", user);
            model.addAttribute("loginUserId", user.getUserId());

            boolean isLiked = kakaoShowGymData.isAlreadyLiked(gymId, user.getUserId());
            model.addAttribute("isLiked", isLiked);  // 💡 버튼 상태 유지 핵심
        } else {
            model.addAttribute("loginUserId", null);
            model.addAttribute("isLiked", false);    // 로그인 안 했으면 찜 아님
        }

        return "healthGym/detail";
    }



    @GetMapping("/gyms/compare")
    public String showComparePage(@RequestParam("gymId") List<Integer> gymIds, Model model, @AuthenticationPrincipal SecurityUser securityUser) {

        List<DetailDataDto> gymList = kakaoShowGymData.detailFormList(gymIds);

        if (securityUser != null) {
            model.addAttribute("loginUserId", securityUser.getUser().getUserId());
            model.addAttribute("countLikes", kakaoShowGymData.countLikes(gymIds));
        } else {
            model.addAttribute("loginUserId", null);
        }
        model.addAttribute("detailDataList", gymList);

        Map<Integer, Integer> like = kakaoShowGymData.countLikes(gymIds);
        model.addAttribute("like", like);

        return "healthGym/recentGyms";
    }
}
