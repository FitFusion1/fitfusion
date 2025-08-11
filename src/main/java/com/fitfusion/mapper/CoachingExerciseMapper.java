package com.fitfusion.mapper;

import com.fitfusion.vo.CoachingExercise;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CoachingExerciseMapper {

    List<CoachingExercise> getAllCoachingExercises();
    CoachingExercise getCoachingExerciseById(int id);
    String getCoachingExerciseNameById(int id);
    String getCoachingExerciseTitleById(int id);

}