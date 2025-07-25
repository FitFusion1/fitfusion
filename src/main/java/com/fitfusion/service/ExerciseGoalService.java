package com.fitfusion.service;

import com.fitfusion.enums.GoalType;
import com.fitfusion.mapper.SelectedGoalMapper;
import com.fitfusion.vo.ExerciseGoal;
import com.fitfusion.mapper.ExerciseGoalMapper;
import com.fitfusion.vo.SelectedGoal;
import com.fitfusion.web.form.ExerciseGoalRegisterForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ExerciseGoalService {


    private final ExerciseGoalMapper exerciseGoalMapper;
    private final SelectedGoalService selectedGoalService;


    public void insertUserGoalWithSelection(ExerciseGoalRegisterForm formData) {
        exerciseGoalMapper.insertUserGoal(formData);
        selectedGoalService.selectGoal(formData.getUserId(), formData.getGoalId());
    }

    public void insertUserGoal(ExerciseGoalRegisterForm formData) {

        exerciseGoalMapper.insertUserGoal(formData);
    }

    public ExerciseGoal getUserGoalByUserId(int userId, int goalId) {
        return exerciseGoalMapper.getUserGoalByUserIdAndGoalId(userId, goalId);
    }

    public void updateGoal(ExerciseGoalRegisterForm form) {
        ExerciseGoal exerciseGoal = exerciseGoalMapper.getUserGoalByUserIdAndGoalId(form.getUserId(), form.getGoalId());

        GoalType goalType = GoalType.valueOf(form.getGoalType());
        exerciseGoal.setGoalType(goalType.getGoalName());

        exerciseGoal.setStartWeight(form.getStartWeight());
        exerciseGoal.setTargetWeight(form.getTargetWeight());
        exerciseGoal.setGoalDescription(form.getGoalDescription());
        exerciseGoal.setStartDate(form.getStartDate());
        exerciseGoal.setEndDate(form.getEndDate());

        exerciseGoalMapper.updateGoal(exerciseGoal);
    }

    public void deleteGoal(int goalId) {
        selectedGoalService.deleteSelectedGoal(goalId);
        exerciseGoalMapper.deleteGoal(goalId);
    }

    public ExerciseGoal getSelectedGoalByUserId(int userId) {
        return exerciseGoalMapper.findSelectedGoalByUserId(userId);
    }

}
