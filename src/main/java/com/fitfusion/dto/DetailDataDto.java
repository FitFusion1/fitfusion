package com.fitfusion.dto;

import com.fitfusion.vo.GymReview;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.Alias;

import java.util.List;

@Getter
@Setter
@Alias("DetailDataDto")
public class DetailDataDto {

    // 상세정보 dto
    private int gymId;
    private String name;
    private String address;
    private String phone;
    private Double latitude;
    private Double longitude;
    private double averageRating;
    private int reviewCount;
    private List<GymReviewDto> gymReviews;
}
