package com.fitfusion.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RoutineExercise {
    private int routinExerciseId;
    private int routineId;
    private int exerciseId;
    private int sets;
    private int reps;
    private int weight;
}
