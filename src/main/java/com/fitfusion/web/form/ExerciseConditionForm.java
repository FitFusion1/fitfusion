package com.fitfusion.web.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class ExerciseConditionForm {
    private List<String> avoidParts;
    @NotEmpty(message = "운동하고 싶은 부위를 한 개 이상 선택해주세요.")
    private List<String> targetParts;
    @NotBlank(message = "컨디션을 선택해주세요.")
    private String conditionLevel;
}
