package com.fitfusion.vo;

import lombok.Data;

import java.util.List;

@Data
public class ExerciseCondition {
    private List<String> avoidParts;
    private List<String> targetParts;
    private String conditionLevel;
}
