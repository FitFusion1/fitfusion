package com.fitfusion.mapper;

import com.fitfusion.dto.ExerciseLogDto;
import com.fitfusion.vo.ExerciseLog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ExerciseLogMapper {

    void insertExerciseLog(ExerciseLog log);

    List<ExerciseLogDto> findExerciseLog(int userId);

    List<ExerciseLogDto> findRoutineLogDetail(int userId);
}
