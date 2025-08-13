package com.fitfusion.service;

import com.fitfusion.dto.ExerciseGoalDto;
import com.fitfusion.enums.GoalType;
import com.fitfusion.vo.ExerciseGoal;
import com.fitfusion.mapper.ExerciseGoalMapper;
import com.fitfusion.web.form.ExerciseGoalRegisterForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ExerciseGoalService {


    private final ExerciseGoalMapper exerciseGoalMapper;
    private final SelectedGoalService selectedGoalService;


    public List<ExerciseGoalDto> getAllGoalByUserId(int userId) {

        List<ExerciseGoal> goals = exerciseGoalMapper.getAllUserGoalsByUserId(userId);

        return goals.stream()
                    .map(ExerciseGoalDto::new)
                    .toList();
    }

    @Transactional
    public void insertUserGoalWithSelection(ExerciseGoalRegisterForm formData) {
        exerciseGoalMapper.insertUserGoal(formData);
        selectedGoalService.selectGoal(formData.getUserId(), formData.getGoalId());
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

    @Transactional
    public void deleteGoal(int goalId) {
        selectedGoalService.deleteSelectedGoal(goalId);
        exerciseGoalMapper.deleteGoal(goalId);
    }

    public ExerciseGoal getSelectedGoalEntityByUserId(int userId) {
        return exerciseGoalMapper.findSelectedGoalByUserId(userId);
    }
    public ExerciseGoalDto getSelectedGoalDtoByUserId(int userId) {
        ExerciseGoal goal = exerciseGoalMapper.findSelectedGoalByUserId(userId);

        return new ExerciseGoalDto(goal);
    }

}
