package com.fitfusion.dto;

import lombok.Data;

@Data
public class RoutineListDto {
    private int routineId;
    private String routineName;
    private String targetPart;
    private String difficulty;
    private String totalTime;
    private String summary;
    private int exerciseCount;
}
