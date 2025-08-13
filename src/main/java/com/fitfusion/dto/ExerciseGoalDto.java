package com.fitfusion.dto;

import com.fitfusion.vo.ExerciseGoal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseGoalDto {
    private String goalType;
    private Date endDate;
    private Date startDate;
    private int goalId;
    private int startWeight;
    private int targetWeight;

    public ExerciseGoalDto(ExerciseGoal goal) {
        this.goalId = goal.getGoalId();
        this.goalType = goal.getGoalType();
        this.endDate = goal.getEndDate();
        this.startDate = goal.getStartDate();
        this.startWeight = goal.getStartWeight();
        this.targetWeight = goal.getTargetWeight();
    }
}
