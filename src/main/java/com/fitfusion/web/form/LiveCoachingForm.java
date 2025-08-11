package com.fitfusion.web.form;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.ibatis.type.Alias;

@Getter
@Setter
@NoArgsConstructor
@Alias("LiveCoachingForm")
public class LiveCoachingForm {

    @NotNull(message = "운동을 선택해주세요.")
    private Integer exerciseId;

    private String exerciseName;
    private String exerciseTitle;

    @NotNull(message = "목표 세트 수를 입력해주세요.")
    @Min(value = 1, message = "1세트 이상 설정해주세요.")
    @Max(value = 10, message = "10세트 이하로 설정해주세요.")
    private int targetSets;

    @NotNull(message = "목표 반복 횟수를 입력해주세요.")
    @Min(value = 1, message = "1회 이상 입력해주세요.")
    @Max(value = 100, message = "100회 이하로 설정해주세요.")
    private int targetReps;

    private int targetTime;
}
