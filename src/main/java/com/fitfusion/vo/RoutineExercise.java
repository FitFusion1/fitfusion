package com.fitfusion.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class RoutineExercise {
    private int routinExerciseId;
    private int routineId;
    private int exerciseId;
    private int sets;
    private int reps;
    private int weight;
}
