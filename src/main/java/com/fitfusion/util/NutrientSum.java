package com.fitfusion.util;

import com.fitfusion.dto.NutrientSummaryDto;
import com.fitfusion.dto.SelectedFoodDto;

import java.util.List;

public class NutrientSum {

    // 선택한 음식 리스트의 영양소 합계 계산
    public static NutrientSummaryDto sum(List<SelectedFoodDto> foods) {
        double totalCalories = 0;
        double totalCarbs = 0;
        double totalProtein = 0;
        double totalFat = 0;

        for (SelectedFoodDto food : foods) {
            totalCalories += food.getCalories();
            totalCarbs += food.getCarbohydrateG();
            totalProtein += food.getProteinG();
            totalFat += food.getFatG();
        }

        return NutrientSummaryDto.builder()
                .calories(totalCalories)
                .carbohydrateG(totalCarbs)
                .proteinG(totalProtein)
                .fatG(totalFat)
                .build();
    }
}
