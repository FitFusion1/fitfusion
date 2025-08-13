package com.fitfusion.mapper;

import com.fitfusion.vo.CoachingExercise;
import com.fitfusion.vo.Exercise;
import com.fitfusion.vo.Video;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExerciseListMapper {

    int countExercises();

    int countCoachingExercises();

    int getCategoryCount();

    int getEquipmentCount();

    List<Exercise> getAllExercises();

    List<CoachingExercise> getAllCoachingExercises();

    Exercise getExerciseById(@Param("id") int id);

    Video getLatestByExerciseId(@Param("exerciseId") int exerciseId);
}
