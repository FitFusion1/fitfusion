package com.fitfusion.service;

import com.fitfusion.mapper.MealRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class MealRecordService {
    private final MealRecordMapper mealRecordMapper;

    public Map<String, Double> getTodayNutrientSummary(int userId) {
        return mealRecordMapper.getTodayNutrientSummary(userId);
    }
}
