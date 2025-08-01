package com.fitfusion.mapper;

import com.fitfusion.dto.MealRecordDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface MealRecordMapper {
    Map<String, Double> getTodayNutrientSummary(int userId);
    void insertMealRecord(MealRecordDto mealRecord);
}
