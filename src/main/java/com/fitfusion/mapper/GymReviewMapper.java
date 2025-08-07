package com.fitfusion.mapper;

import com.fitfusion.dto.GymReviewDto;
import com.fitfusion.dto.PagedGymReviewDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GymReviewMapper {

    public void insertReview(GymReviewDto gymReviewDto);

    List<GymReviewDto> selectReviewsByGymId(int gymId);

    void updateReview(GymReviewDto gymReviewDto);

    void deleteReview(GymReviewDto gymReview);

    List<PagedGymReviewDto> selectReviewsPaged(@Param("gymId") int gymId, @Param("offset") int offset, @Param("size") int size);
}
