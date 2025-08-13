package com.fitfusion.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.Alias;

@Getter
@Setter
@Alias( "GymDataDto")
public class GymDataDto {

    // 헬스장 정보 dto
    private int gymId;
    private String name;
    private String address;
    private String phone;
    private Double latitude;
    private Double longitude;
    private String kakaoPlaceId;
    private double averageRating;
    private String profileUrl;
    private String distance;
}
