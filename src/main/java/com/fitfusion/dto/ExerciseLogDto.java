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
    private int weight;
    private Date logDate;
    private String formattedDate;
    private String isChecked;
    private String exerciseName;
//    @NotNull(message = "세트 수는 필수 입력 값입니다.")
//    @Min(value = 1, message = "세트 수는 1 이상이어야 합니다.")
    private Integer sets;
//    @NotNull(message = "반복 수는 필수 입력 값입니다.")
//    @Min(value = 1, message = "반복 수는 1 이상이어야 합니다.")
    private Integer reps;
    private Integer durationMinutes;
    private Integer recommendedSets;
    private Integer recommendedReps;
    private Exercise exercise;
    private String routineName;

}
