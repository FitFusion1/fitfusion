package com.fitfusion.mapper;

import com.fitfusion.vo.TargetPart;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TargetPartsMapper {
    void insertTargetPart(TargetPart targetPart);
    void deleteTargetPartsByUserId(int userId);
}
