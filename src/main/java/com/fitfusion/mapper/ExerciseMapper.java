package com.fitfusion.mapper;

import com.fitfusion.vo.Exercise;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ExerciseMapper {
    void insertExercise(Exercise exercise);

    List<Exercise> findAllExercises();

    Exercise findExerciseById(int id);
}
