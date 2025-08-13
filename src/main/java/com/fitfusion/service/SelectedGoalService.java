package com.fitfusion.service;

import com.fitfusion.dto.ExerciseGoalDto;
import com.fitfusion.dto.SelectGoalResponseDto;
import com.fitfusion.mapper.ExerciseGoalMapper;
import com.fitfusion.mapper.SelectedGoalMapper;
import com.fitfusion.vo.SelectedGoal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SelectedGoalService {


    private final SelectedGoalMapper selectMapper;
    private final ExerciseGoalMapper exerciseGoalMapper;
    private final ExerciseConditionService exerciseConditionService;

    public SelectGoalResponseDto selectGoalForResponse(int userId, int goalId) {
        selectGoal(userId, goalId);

        ExerciseGoalDto dto = new ExerciseGoalDto(
                exerciseGoalMapper.findSelectedGoalByUserId(userId)
        );
        boolean isMuscleGoal = "근육 증가".equals(dto.getGoalType());
        if (!isMuscleGoal) {
            exerciseConditionService.deleteConditionByUserId(userId);
        }
        return new SelectGoalResponseDto("목표가 선택되었습니다!", isMuscleGoal);
    }

    public void selectGoal(int userId, int goalId) {
        SelectedGoal existing = selectMapper.findByUserId(userId);
        SelectedGoal newSelection = new SelectedGoal();
        newSelection.setUserId(userId);
        newSelection.setGoalId(goalId);

        if (existing == null) {
            selectMapper.insertSelectedGoal(newSelection);
        } else {
            selectMapper.updateSelectedGoal(newSelection);
        }
    }

    public SelectedGoal getSelectedGoal(int userId) {
        return selectMapper.findByUserId(userId);
    }


    public void deleteSelectedGoal(int userId) {
        selectMapper.deleteByUserId(userId);
    }

}
