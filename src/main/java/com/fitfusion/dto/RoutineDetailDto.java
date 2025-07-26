package com.fitfusion.dto;

import com.fitfusion.vo.Exercise;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RoutineDetailDto {
    private int routineId;
    private String description;
    private int exerciseId;
    private int routineExerciseId;
    private String routineName;
    private String targetPart;
    private String totalTime;
    private int totalExercises;
    private int defaultSet;
    private List<ExerciseItemDto> exercises;
}
