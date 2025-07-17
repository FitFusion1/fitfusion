package com.fitfusion.dto;

import lombok.Data;

@Data
public class RoutineListDto {
    int routineId;
    String routineName;
    String targetPart;
    String difficulty;
    String totalTime;
    String summary;
}
