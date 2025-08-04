package com.fitfusion.dto;

import com.fitfusion.vo.ExerciseGoal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseGoalDto {
    private String goalType;
    private int goalId;
    private int startWeight;
    private int targetWeight;

    public ExerciseGoalDto(ExerciseGoal goal) {
        this.goalId = goal.getGoalId();
        this.goalType = goal.getGoalType();
        this.startWeight = goal.getStartWeight();
        this.targetWeight = goal.getTargetWeight();
    }
}
