package com.fitfusion.mapper;

import com.fitfusion.dto.DetailDataDto;
import com.fitfusion.dto.GymDataDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface GymMapper {

    void insert(GymDataDto gymplaceData);

    GymDataDto select(@Param("kakaoPlaceId") String kakaoPlaceId);

    DetailDataDto detailData(int gymId);

}
