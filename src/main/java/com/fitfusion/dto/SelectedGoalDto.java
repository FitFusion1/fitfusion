package com.fitfusion.dto;


import com.fitfusion.vo.SelectedGoal;
import lombok.Data;

@Data
public class SelectedGoalDto {
    private int goalId;
    private int userId;

    public SelectedGoalDto(SelectedGoal selectedGoal) {
        this.goalId = selectedGoal.getGoalId();
        this.userId = selectedGoal.getUserId();
    }
}
