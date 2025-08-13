package com.fitfusion.vo;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoutineExercise {
    private int routineExerciseId;
    private int routineId;
    private int exerciseId;
    private int sets;
    private int reps;
    private int weight;
}
