package com.fitfusion.mapper;

import com.fitfusion.vo.AvoidPart;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AvoidPartsMapper {

    void deleteAvoidPartsByUserId(int userId);

    void insertAvoidPart(AvoidPart avoidPart);

}
