package com.fitfusion.web.controller;

import com.fitfusion.dto.DetailDataDto;
import com.fitfusion.dto.GymReviewDto;
import com.fitfusion.security.SecurityUser;
import com.fitfusion.service.KakaoGymSearchService;
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
    private final KakaoGymSearchService kakaoGymSearchService;

    /**
     * 지도 접근하는 컨트롤러
     * @param model 사용자 로그인 유뮤 전달
     * @param securityUser 사용자 정보
     * @return healthGym/map 반환
     */
    @GetMapping("/map")
    public String map(Model model, @AuthenticationPrincipal SecurityUser securityUser) {

        if (securityUser != null ) {
            model.addAttribute("loginUserId", securityUser.getUser().getUserId());
        } else {
            model.addAttribute("loginUserId", null);
        }
        return "healthGym/map";
    }

    /**
     * 헬스장 상세 페이지 컨트롤러
     * @param gymId 고유 식별 번호를 @PathVariable로 받는다
     * @param securityUser 사용자 정보
     * @param model 찜 등록 상태 및 로그인 정보 담아서 전달
     * @return healthGym/detail 반환
     */
    @GetMapping("/gyms/detail/{gymId}")
    public String detail(@PathVariable int gymId,
                         @AuthenticationPrincipal SecurityUser securityUser,
                         Model model) {

        DetailDataDto detailData = kakaoShowGymData.detailForm(gymId);
        model.addAttribute("detailData", detailData);
        model.addAttribute("fiveReviews", detailData.getGymReviews());
        model.addAttribute("totalReviews", detailData.getReviewCount());

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


    /**
     * 선택한 헬스장들의 비교 페이지 요청을 처리합니다.
     *
     * 헬스장의 속성 정보, 찜 정보, 로그인한 사용자 ID(로그인되어 있을 경우)를 모델에 추가합니다.
     * 이 페이지에서는 사용자가 여러 헬스장의 정보를 확인하고 비교할 수 있습니다.
     *
     * @param gymIds 비교할 헬스장들의 ID 리스트
     * @param model 뷰로 데이터를 전달하는 데 사용되는 모델 객체
     * @param securityUser 인증된 사용자의 보안 정보. 로그인하지 않은 경우 null일 수 있습니다.
     * @return 헬스장 비교 페이지에 해당하는 뷰 템플릿 이름
     */
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
