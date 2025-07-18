package com.fitfusion.service;

import com.fitfusion.dto.GymReviewDto;
import com.fitfusion.mapper.GymReviewMapper;
import com.fitfusion.vo.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class GymReviewService {

    private final GymReviewMapper gymReviewMapper;

    public GymReviewDto insertReview(GymReviewDto gymReviewDto) {

        gymReviewMapper.insertReview(gymReviewDto);
        System.out.println("gymReviewDto.getReviewId()" + gymReviewDto.getReviewId());
        return gymReviewDto;
    }

    public GymReviewDto updateReview(int commentId, GymReviewDto gymReviewDto, User user ) {

        Date now = new Date();

        gymReviewDto.setUserId(user.getUserId());
        gymReviewDto.setReviewId(commentId);
        gymReviewDto.setUpdatedDate(now);


        gymReviewMapper.updateReview(gymReviewDto);

        return gymReviewDto;
    }


    public GymReviewDto deleteReview(int commentId, User user) {

        GymReviewDto gymReview = new GymReviewDto();
        gymReview.setReviewId(commentId);

        gymReviewMapper.deleteReview(gymReview);

        return gymReview;
    }
}
