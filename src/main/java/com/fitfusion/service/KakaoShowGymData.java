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

import java.util.*;

@Service
@RequiredArgsConstructor
public class KakaoShowGymData {

    private final GymMapper gymMapper;
    private final GymReviewMapper gymReviewMapper;
    private final GymLikeMapper gymLikeMapper;

    // 1. 저장만 하는 메서드
    public void insertGym(GymDataDto gymDataDto) {

        GymDataDto kakaoPlaceData = gymMapper.select(gymDataDto.getKakaoPlaceId());
        if (kakaoPlaceData != null) {
        } else {
            gymMapper.insert(gymDataDto);
        }
    }

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


    public List<DetailDataDto> detailFormList(List<Integer> gymIds) {
        List<DetailDataDto> result = new ArrayList<>();
        for (int gymId : gymIds) {
            DetailDataDto dto = detailForm(gymId); // 기존 detailForm 재활용
            result.add(dto);
        }
        return result;
    }

    public DetailDataDto detailForm(int gymId) {
        DetailDataDto dto = gymMapper.detailData(gymId);

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

    public boolean isAlreadyLiked(int gymId, int userId) {
        Integer count = gymLikeMapper.selectLike(gymId, userId);
        System.out.println("cout:" + count);
        return count != null && count > 0;

    }

    public void deleteLike(int gymId, User user) {
        System.out.println("ServiceUserId:" + user.getUserId());
        gymLikeMapper.deleteLike(gymId, user.getUserId());
        System.out.println("Service2UserId:" + user.getUserId());
    }

    public Map<Integer, Integer> countLikes(List<Integer> gymIds) {
            Map<Integer, Integer> countLikesMap = new HashMap<>();
        for (int gymId : gymIds) {
            int count = gymLikeMapper.countLike(gymId);
            countLikesMap.put(gymId, count);
        }
        return countLikesMap;
    }
}

