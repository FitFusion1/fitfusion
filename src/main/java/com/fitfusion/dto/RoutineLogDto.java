package com.fitfusion.dto;

import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Data
public class RoutineLogDto {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private int logId;
    private int routineId;
    private int sessionId;
    @Valid
    private List<ExerciseLogDto> exerciseLogs;
    private int recommendedSets;
    private int recommendedReps;
    private String routineName;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date logDate;

    public String getFormattedDate() {
        if (exerciseLogs.isEmpty()) {
            return "";
        }
        return DATE_FORMAT.format(exerciseLogs.getFirst().getLogDate());
    }
}
