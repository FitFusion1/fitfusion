package com.fitfusion.vo;

import lombok.Data;

@Data
public class RecommendedExercise {
    private Integer exerciseId;
    private Integer routineExerciseId;
    private int sets;
    private int reps;
    private int weight;
    private int routineId;

    private Exercise exercise;
}
