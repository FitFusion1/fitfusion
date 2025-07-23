package com.fitfusion.dto;

import jakarta.validation.Valid;
import lombok.Data;

import java.util.List;

@Data
public class RoutineLogDto {
    private int logId;
    private int routineId;

    @Valid
    private List<ExerciseLogDto> exerciseLogs;
    private int recommendedSets;
    private int recommendedReps;
}
