package com.fitfusion.web.form;

import lombok.Data;

import java.util.List;

@Data
public class ExerciseConditionForm {
    private List<String> avoidParts;
    private List<String> targetParts;
    private String conditionLevel;
}
