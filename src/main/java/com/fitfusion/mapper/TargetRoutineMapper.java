package com.fitfusion.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TargetRoutineMapper {

    List<String> getLeastUsedParts(int userId);
}
