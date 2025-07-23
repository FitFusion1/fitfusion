package com.fitfusion.mapper;

import com.fitfusion.vo.AvoidPart;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AvoidPartsMapper {

    void deleteAvoidPartsByUserId(int userId);
    void insertAvoidPart(AvoidPart avoidPart);
    List<String> getAvoidPartsByUserId(int userId);

    void deleteAvoidPartsByConditionId(int conditionId);
}
