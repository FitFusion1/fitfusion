package com.fitfusion.mapper;

import com.fitfusion.vo.ExerciseGoal;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ExerciseGoalMapper {

    void insertUserGoal(ExerciseGoal goal);
}
