package com.fitfusion.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GoalsResponseDto {
    private List<ExerciseGoalDto> goals;
    private SelectedGoalDto selectedGoal;
}
