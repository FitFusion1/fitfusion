package com.fitfusion.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ExerciseStatusDto {
    private String monthlyTotalTime;
    private int consistencyScore;
    private int totalRoutineCount;
    private int completedRoutineSessions;
    private long averageComletionRate;
    private String mostUsedPart;
    private String leastUsedPart;
    private List<String> dailyLabels;
    private List<Integer> dailyData;
    private List<String> balanceLabels;
    private List<Integer> balanceData;
    private List<Map<String, Object>> personalRecords;
}
