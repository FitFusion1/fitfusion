package com.fitfusion.dto;

import com.fitfusion.vo.RecommendedExercise;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveRecommendRoutineRequestDto {
    @NotBlank
    private String routineName;
    @NotEmpty
    private List<RecommendedExercise> exercises;
}
