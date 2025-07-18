package com.fitfusion.web.controller;

import com.fitfusion.dto.DetailDataDto;
import com.fitfusion.dto.GymDataDto;
import com.fitfusion.dto.GymReviewDto;
import com.fitfusion.dto.KakaoplaceDto;
import com.fitfusion.security.SecurityUser;
import com.fitfusion.service.GymReviewService;
import com.fitfusion.service.KakaoGymSearchService;
import com.fitfusion.service.KakaoShowGymData;
import com.fitfusion.vo.Gym;
import com.fitfusion.vo.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class MapController {

    private final KakaoGymSearchService kakaoGymSearchService;
    private final KakaoShowGymData kakaoShowGymData;
    private final GymReviewService gymReviewService;

    /**
     * 나의 위치 주변 헬스장 정보를 받아오기
     *
     * @param latitude  위도
     * @param longitude 경도
     * @return 주변 헬스장 목록
     */
    @GetMapping("/api/kakao/gyms")
    public List<KakaoplaceDto> UserNearByGyms(@RequestParam("lat") Double latitude, @RequestParam("lon") Double longitude) {
        String keyword = "헬스장";
        return kakaoGymSearchService.searchGym(keyword, latitude, longitude);
    }

    /**
     * 마커 클릭시 저장되는 헬스장정보 gymDataDto에 담아서 전달
     *
     * @param gymDataDto 헬스장 정보
     * @return 저장된 헬스장 정보 반환 (kakaoPlaceId 기준)
     */
    @PostMapping("/api/gyms")
    public GymDataDto saveGym(@RequestBody GymDataDto gymDataDto) {

        kakaoShowGymData.insertGym(gymDataDto);
        System.out.println("gymDataDto: " + gymDataDto);
        System.out.println("kakaoPlaceId: " + gymDataDto.getKakaoPlaceId());
        return kakaoShowGymData.selectByKakaoPlaceId(gymDataDto.getKakaoPlaceId());
    }

    /**
     * 헬스장 레뷰를 gymReviewDto에 전달받아서 생성하는 컨트롤
     *
     * @param gymReviewDto 리뷰dto
     * @param securityUser 로그인한 사용자 정보
     * @return 등록된 리뷰 정보 반환
     */
    @PostMapping("/gym/review")
    public GymReviewDto createReview(@RequestBody GymReviewDto gymReviewDto, @AuthenticationPrincipal SecurityUser securityUser) {
        System.out.println("getContent: " + gymReviewDto.getContent());
        System.out.println("getReviewId: " + gymReviewDto.getReviewId());
        System.out.println("getRating: " + gymReviewDto.getRating());
        System.out.println("getRating: " + gymReviewDto.getGymId());

        Date date = new Date();

        User user = securityUser.getUser();
        gymReviewDto.setUser(user);

        gymReviewDto.setCreatedDate(date);
        gymReviewDto.setUpdatedDate(date);

        return gymReviewService.insertReview(gymReviewDto);
    }

    @PutMapping("/gym/review/{commentId}")
    public ResponseEntity<GymReviewDto> updateReview(@PathVariable int commentId, @RequestBody GymReviewDto gymReviewDto, @AuthenticationPrincipal SecurityUser securityUser) {

        User user = securityUser.getUser();
        System.out.println("user: " + user.getUsername());
        System.out.println("commentId: " + commentId);
        System.out.println("gymReviewDto: " + gymReviewDto);
        System.out.println("gymReviewDto.getContent()" + gymReviewDto.getContent());

        GymReviewDto updateReview = gymReviewService.updateReview(commentId, gymReviewDto, user);

        return ResponseEntity
                .ok(updateReview);
    }

    @DeleteMapping("/gym/review/{commentId}")
    public ResponseEntity<GymReviewDto> deleteReview(@PathVariable int commentId, @AuthenticationPrincipal SecurityUser securityUser) {

        User user = securityUser.getUser();

        GymReviewDto deleteReview = gymReviewService.deleteReview(commentId, user);

        return ResponseEntity
                .ok(deleteReview);
    }
}

