package com.fitfusion.mapper;

import com.fitfusion.dto.GymReviewDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GymReviewMapper {

    public void insertReview(GymReviewDto gymReviewDto);

    List<GymReviewDto> selectReviewsByGymId(int gymId);

    void updateReview(GymReviewDto gymReviewDto);

    void deleteReview(GymReviewDto gymReview);
}
