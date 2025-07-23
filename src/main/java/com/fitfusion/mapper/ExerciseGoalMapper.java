package com.fitfusion.mapper;

import com.fitfusion.vo.ExerciseGoal;
import com.fitfusion.web.form.ExerciseGoalRegisterForm;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ExerciseGoalMapper {

    ExerciseGoal findSelectedGoalByUserId(int userId);

    void insertUserGoal(ExerciseGoalRegisterForm goal);

    ExerciseGoal getUserGoalByUserIdAndGoalId(int userId, int goalId);

    List<ExerciseGoal> getAllUserGoalsByUserId(int userId);

    void updateGoal(ExerciseGoal goal);

    void deleteGoal(int goalId);
}
