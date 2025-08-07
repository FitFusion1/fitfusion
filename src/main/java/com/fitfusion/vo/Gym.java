package com.fitfusion.vo;

import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.Alias;

import java.util.Date;

@Getter
@Setter
@Alias("Gym")
public class Gym {

    private int gymId;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private int phone;
    private String kakaoPlaceId;
    private Date createdDate;
    private Date updatedDate;
    private Double averageRating;
    private String profileUrl;

}
