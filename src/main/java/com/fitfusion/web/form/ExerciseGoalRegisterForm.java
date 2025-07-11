package com.fitfusion.web.form;

import com.fitfusion.enums.GoalType;
import com.fitfusion.validation.Step1Group;
import com.fitfusion.validation.Step2Group;
import com.fitfusion.validation.Step3Group;
import com.fitfusion.validation.Step4Group;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
public class ExerciseGoalRegisterForm {

    private int userId;

    @NotNull(message = "목표는 필수 선택값입니다.", groups = Step1Group.class)
    private GoalType goalType;

    @NotBlank(message = "설명은 필수 입력값입니다.", groups = Step4Group.class)
    @Size(min = 1, message = "설명은 최소 1글자 이상입니다.")
    private String goalDescription;

    @Min(value = 1, message = "현재 체중은 1kg 이상이어야 합니다.", groups = Step2Group.class)
    private Integer startWeight;

    @Min(value = 1, message = "목표 체중은 1kg 이상이어야 합니다.", groups = Step2Group.class)
    private Integer targetWeight;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "운동 시작일을 선택해주세요.", groups = Step3Group.class)
    private Date startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "운동 시작일을 선택해주세요.", groups = Step3Group.class)
    private Date endDate;
}
