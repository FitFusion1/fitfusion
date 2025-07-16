package com.fitfusion.mapper;

import com.fitfusion.vo.TargetPart;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TargetPartsMapper {
    void insertTargetPart(TargetPart targetPart);
    void deleteTargetPartsByUserId(int userId);
    List<String> getTargetPartsByUserId(int userId);
}
