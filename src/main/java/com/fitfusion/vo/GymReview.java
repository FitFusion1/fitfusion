package com.fitfusion.vo;

import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.Alias;

import java.util.Date;

@Getter
@Setter
@Alias("GymReview")
public class GymReview {

    private int reviewId;
    private int rating;
    private String content;
    private Date createdDate;
    private Date updatedDate;

    private User userId;
    private Gym gymId;
}
