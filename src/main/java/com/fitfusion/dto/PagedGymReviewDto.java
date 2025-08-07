package com.fitfusion.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.Alias;

@Getter
@Setter
@Alias("PagedGymReviewDto")
public class PagedGymReviewDto {
    private int reviewId;
    private Double rating;
    private String content;
    private String createdDate;
    private String username; // 작성자 이름만
    private int userId;
}

