package com.fitfusion.dto;

import com.fitfusion.vo.RecommendedExercise;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationResponseDto {
    private String routineName;
    private List<RecommendedExercise> exercises;
}
