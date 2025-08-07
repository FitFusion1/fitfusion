package com.fitfusion.service;

import com.fitfusion.dto.DetailDataDto;
import com.fitfusion.dto.GymDataDto;
import com.fitfusion.dto.GymReviewDto;
import com.fitfusion.mapper.GymLikeMapper;
import com.fitfusion.mapper.GymMapper;
import com.fitfusion.mapper.GymReviewMapper;
import com.fitfusion.vo.Gym;
import com.fitfusion.vo.GymLikes;
import com.fitfusion.vo.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KakaoShowGymData {

    private final GymMapper gymMapper;
    private final GymReviewMapper gymReviewMapper;
    private final GymLikeMapper gymLikeMapper;


    /**
     * 카카오 Place ID를 기준으로 해당 헬스장이 데이터베이스에 존재하지 않을 경우, 새로운 헬스장을 삽입합니다.
     * 이 메서드는 먼저 주어진 카카오 Place ID를 통해 헬스장이 데이터베이스에 이미 존재하는지를 확인합니다.
     * 존재할 경우 아무 작업도 하지 않으며, 존재하지 않을 경우 해당 헬스장 데이터를 삽입합니다.
     *
     * @param gymDataDto 삽입할 헬스장 데이터를 담고 있는 객체로, 존재 여부 확인에 사용할 카카오 Place ID를 포함합니다.
     */
    public void insertGym(GymDataDto gymDataDto) {

        GymDataDto kakaoPlaceData = gymMapper.select(gymDataDto.getKakaoPlaceId());
        if (kakaoPlaceData != null) {
            return;
        } else {
            gymMapper.insert(gymDataDto);
        }
    }

    /**
     * 주어진 Kakao Place ID를 이용하여 헬스장 데이터를 조회합니다.
     * 고객 리뷰가 있는 경우, 이를 기반으로 평균 평점을 계산하여 설정합니다.
     * 리뷰가 없는 경우, 평균 평점은 0.0으로 설정됩니다.
     *
     * @param kakaoPlaceId 카카오 DB에 등록된 헬스장을 식별하는 고유 ID
     * @return 평균 평점이 계산된 GymDataDto 객체 형태의 헬스장 데이터
     */
    public GymDataDto selectByKakaoPlaceId(String kakaoPlaceId) {

        GymDataDto gym = gymMapper.select(kakaoPlaceId);

        List<GymReviewDto> reviews = gymReviewMapper.selectReviewsByGymId(gym.getGymId());

        if (reviews != null && !reviews.isEmpty()) {
            double sum = 0;
            int count = 0;

            for (GymReviewDto r : reviews) {
                if (r.getRating() != null) {
                    sum += r.getRating();
                    count++;
                }
            }

            double avg = Math.round((sum / count) * 10) / 10.0;
            gym.setAverageRating(avg);
        } else {
            gym.setAverageRating(0.0);
        }

        return gym;
    }


    /**
     * 지정된 헬스장 ID 목록에 대해 상세 정보를 조회합니다.
     * 이 메서드는 전달받은 헬스장 ID들을 순회하며, 각 ID에 대해 detailForm 메서드를 사용해 상세 데이터를 조회하고,
     * 그 결과를 리스트에 모아 반환합니다.
     *
     * @param gymIds 상세 정보를 조회할 헬스장 ID(Integer)들의 목록
     * @return 각 헬스장에 대한 상세 정보가 담긴 DetailDataDto 객체 리스트
     */
    public List<DetailDataDto> detailFormList(List<Integer> gymIds) {
        List<DetailDataDto> result = new ArrayList<>();
        for (int gymId : gymIds) {
            DetailDataDto dto = detailForm(gymId); // 기존 detailForm 재활용
            result.add(dto);
        }
        return result;
    }


    /**
     * 특정 헬스장의 상세 정보를 조회합니다.
     * 해당 헬스장에 대한 기본 정보, 최신 리뷰 5개, 평균 평점, 총 리뷰 수를 포함합니다.
     *
     * 리뷰가 존재할 경우, 평균 평점을 계산하고(소수점 첫째 자리까지 반올림),
     * 최신순으로 정렬된 상위 5개의 리뷰만 필터링하여 반환합니다.
     *
     * @param gymId 상세 정보를 조회할 헬스장의 고유 ID
     * @return 헬스장 정보, 최신 리뷰 5개, 평균 평점, 리뷰 수를 포함한 DetailDataDto 객체
     */
    public DetailDataDto detailForm(int gymId) {
        DetailDataDto dto = gymMapper.detailData(gymId);

        List<GymReviewDto> allReview = dto.getGymReviews();

        if(dto.getGymReviews() != null && !dto.getGymReviews().isEmpty()) {
            double sumRating = 0;
            int count = 0;
            double avgRating = 0;

            for(GymReviewDto review : dto.getGymReviews()) {
                if(review.getRating() != null) {
                    sumRating += review.getRating();
                    count++;
                }
            }

            List<GymReviewDto> fiveReviews = allReview.stream()
                    .sorted(Comparator.comparing(GymReviewDto::getCreatedDate).reversed())
                    .limit(5)
                    .collect(Collectors.toList());

            dto.setGymReviews(fiveReviews);
            dto.setReviewCount(count);

            if(count > 0) {
                avgRating = sumRating / count;
                avgRating = Math.round(avgRating * 10) / 10.0;
                dto.setAverageRating(avgRating);
                dto.setReviewCount(count);
            } else {
                dto.setAverageRating(0.0);
                dto.setReviewCount(0);
            }
        }

      return  dto;
    }

    /**
     * 사용자가 특정 헬스장에 대해 "좋아요(찜)"를 등록합니다.
     * 이 메서드는 새로운 GymLikes 객체를 생성하고, 해당 헬스장과 사용자 정보를 연관시켜
     * 데이터베이스에 저장합니다.
     *
     * @param gymId 찜할 헬스장의 ID
     * @param user  헬스장을 찜하는 사용자 객체
     * @return 새로 생성된 찜 정보를 나타내는 GymLikes 객체
     */
    public GymLikes insertLike(int gymId, User user) {

        GymLikes gymLikes = new GymLikes();

        Gym gym = new Gym();
        gym.setGymId(gymId);
        gymLikes.setGym(gym);  // ✔ gym 객체를 직접 주입

        user.setUserId(user.getUserId());
        gymLikes.setUser(user);

        gymLikes.getGym().setGymId(gymId);
        gymLikes.setCreatedDate(new Date());
        gymLikes.setUpdatedDate(new Date());

         gymLikeMapper.insertLike(gymLikes);

         return gymLikes;

    }

    /**
     * 특정 사용자가 지정된 헬스장을 이미 찜(좋아요)했는지 확인합니다.
     *
     * @param gymId 확인할 헬스장의 ID
     * @param userId 확인할 사용자의 ID
     * @return 사용자가 해당 헬스장을 찜했다면 true, 그렇지 않으면 false
     */
    public boolean isAlreadyLiked(int gymId, int userId) {
        Integer count = gymLikeMapper.selectLike(gymId, userId);
        System.out.println("cout:" + count);
        return count != null && count > 0;

    }

    /**
     * 특정 사용자가 특정 헬스장의 찜을 삭제
     * @param gymId 확인할 헬스장의 ID
     * @param user 확인할 사용자의 ID
     */
    public void deleteLike(int gymId, User user) {
        System.out.println("ServiceUserId:" + user.getUserId());
        gymLikeMapper.deleteLike(gymId, user.getUserId());
        System.out.println("Service2UserId:" + user.getUserId());
    }

    /**
     * 전달받은 헬스장 ID 목록에 대해 각 헬스장의 찜(좋아요) 수를 계산합니다.
     * 이 메서드는 데이터베이스에서 각 헬스장 ID에 해당하는 찜 수를 조회한 후,
     * 헬스장 ID를 키로 하고 찜 수를 값으로 가지는 Map 형태로 반환합니다.
     *
     * @param gymIds 찜 수를 조회할 헬스장 ID(Integer)들의 목록
     * @return 각 헬스장 ID를 키, 해당 찜 수를 값으로 가지는 Map 객체
     */
    public Map<Integer, Integer> countLikes(List<Integer> gymIds) {
            Map<Integer, Integer> countLikesMap = new HashMap<>();
        for (int gymId : gymIds) {
            int count = gymLikeMapper.countLike(gymId);
            countLikesMap.put(gymId, count);
        }
        return countLikesMap;
    }
}

