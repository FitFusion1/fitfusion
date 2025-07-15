package com.fitfusion.service;

import com.fitfusion.dto.DetailDataDto;
import com.fitfusion.dto.GymDataDto;
import com.fitfusion.dto.GymReviewDto;
import com.fitfusion.mapper.GymMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KakaoShowGymData {

    private final GymMapper gymMapper;

    // 1. 저장만 하는 메서드
    public void insertGym(GymDataDto gymDataDto) {

        GymDataDto kakaoPlaceData = gymMapper.select(gymDataDto.getKakaoPlaceId());
        if (kakaoPlaceData != null) {
            return;
        } else {
            gymMapper.insert(gymDataDto);
        }
    }

    public GymDataDto selectByKakaoPlaceId(String kakaoPlaceId) {
        return gymMapper.select(kakaoPlaceId);
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
                dto.setAverageRating(avgRating);
                dto.setReviewCount(count);
            } else {
                dto.setAverageRating(0.0);
                dto.setReviewCount(0);
            }
        }

      return  dto;
    }
}

