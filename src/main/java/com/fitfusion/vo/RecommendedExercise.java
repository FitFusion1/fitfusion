package com.fitfusion.vo;

import lombok.Data;

@Data
public class RecommendedExercise {
    private int exerciseId;
    private int routineExerciseId;
    private int sets;
    private int reps;
    private int weight;
    private int routineId;

    private Exercise exercise;
}
