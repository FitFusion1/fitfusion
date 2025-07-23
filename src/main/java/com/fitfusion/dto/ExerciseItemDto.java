package com.fitfusion.dto;

import lombok.Data;

@Data
public class ExerciseItemDto {

    private int exerciseId;
    private int routineExerciseId;
    private int routineId;
    private String name;
    private String category;
    private String parts;
    private String equipment;
    private int fatigueLevel;
    private int sets;
    private int reps;
    private String weight;
    private String description;
    private int durationMinute;
}
