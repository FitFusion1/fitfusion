package com.fitfusion.dto;

import com.fitfusion.vo.Exercise;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class ExerciseLogDto {
    private int exerciseId;
    private int routineExerciseId;
    private int routineId;
    private int userId;
    private int sessionId;
    private int logId;
    private Date logDate;
    private String formattedDate;
    private String isChecked;
    private String exerciseName;
    private Integer sets;
    private Integer reps;
    private Integer weight;
    private Integer durationMinutes;
    private Integer recommendedSets;
    private Integer recommendedReps;
    private Exercise exercise;
    private String routineName;

}
