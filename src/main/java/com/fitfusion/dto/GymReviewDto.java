package com.fitfusion.dto;

import com.fitfusion.vo.Gym;
import com.fitfusion.vo.User;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.Alias;

import java.util.Date;

@Getter
@Setter
@Alias("GymReviewDto")
public class GymReviewDto {

    private int reviewId;
    private Double rating;
    private String content;
    private Date createdDate;
    private Date updatedDate;

    private int userId;
    private int gymId;
    private User user;

}
