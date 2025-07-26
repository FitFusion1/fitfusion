package com.fitfusion.vo;

import com.fitfusion.enums.GoalType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
@ToString
public class ExerciseGoal {
    private int goalId;
    private int userId;
    private String goalName;
    private int progressId;
    private String goalType;
    private String goalDescription;
    private Integer startWeight;
    private Integer targetWeight;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;
}
