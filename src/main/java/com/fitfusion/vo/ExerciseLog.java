package com.fitfusion.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ExerciseLog {
    private int logId;
    private Date logDate;
    private int reps;
    private int weight;
    private int sets;
    private int exerciseId;
    private int userId;
    private boolean isChecked;
    private int durationMinutes;
    private int routineExerciseId;
}
