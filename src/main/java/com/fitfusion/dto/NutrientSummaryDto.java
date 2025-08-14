package com.fitfusion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NutrientSummaryDto {
    /** 총 칼로리 (kcal) */
    private double calories;

    /** 총 탄수화물 (g) */
    private double carbohydrateG;

    /** 총 단백질 (g) */
    private double proteinG;

    /** 총 지방 (g) */
    private double fatG;
}
