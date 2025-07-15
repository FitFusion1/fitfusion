package com.fitfusion.mapper;

import com.fitfusion.vo.Exercise;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ExerciseMapper {
    void insertExercise(Exercise exercise);
}
