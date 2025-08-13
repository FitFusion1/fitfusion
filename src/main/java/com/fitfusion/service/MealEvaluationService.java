package com.fitfusion.service;

import com.fitfusion.dto.NutrientSummaryDto;
import com.fitfusion.enums.DietGoalType;
import com.fitfusion.vo.User;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Service
public class MealEvaluationService {

    // 점수 가중치
    private static final int MAX_CALORIE_SCORE = 70;
    private static final int MAX_NUTRIENT_SCORE = 30;

    // 탄단지 비율 기준 (한국 & AMDR 가이드라인 기반)
    private static final double CARB_MIN = 50;
    private static final double CARB_MAX = 65;
    private static final double PROTEIN_MIN = 15;
    private static final double PROTEIN_MAX = 25;
    private static final double FAT_MIN = 15;
    private static final double FAT_MAX = 25;

    // 목표 칼로리 계산(활동계수 고정)
    public double calculateGoalCalories(User user, DietGoalType goalType) {
        int age = calculateAge(user.getBirthDate());

        // BMR 계산 (Mifflin-St Jeor 공식)
        double bmr = (10 * user.getWeight()) + (6.25 * user.getHeight()) - (5 * age);
        bmr += user.getGender().equalsIgnoreCase("M") ? 5 : -161;

        // 활동계수 (고정)
        double tdee = bmr * 1.3;

        // 목표 유형별 조정
        switch (goalType) {
            case LOSS:
                tdee *= 0.9; // 감량
                break;
            case GAIN:
                tdee *= 1.1; // 증량
                break;
            case MAINTAIN:
            default:
                break; // 유지
        }

        return Math.round(tdee);
    }

    // 식단 점수 계산(칼로리 점수 + 탄단지 점수)
    public int calculateMealScore(double goalCalories, NutrientSummaryDto actual) {
        int calorieScore = calculateCalorieScore(goalCalories, actual.getCalories());
        int nutrientScore = calculateNutrientScore(actual.getCarbohydrateG(), actual.getProteinG(), actual.getFatG());
        return calorieScore + nutrientScore;
    }

    // 칼로리 점수 (±10% = 최고)
    private int calculateCalorieScore(double goalCalories, double actualCalories) {
        double diff = Math.abs(actualCalories - goalCalories);
        double tolerance = goalCalories * 0.1;

        if (diff <= tolerance) return MAX_CALORIE_SCORE;
        if (diff <= tolerance * 2) return (int)(MAX_CALORIE_SCORE * 0.7);
        return (int)(MAX_CALORIE_SCORE * 0.4);
    }

    // 탄단지 점수
    private int calculateNutrientScore(double carbohydrateG, double proteinG, double fatG) {
        double total = carbohydrateG + proteinG + fatG;
        if (total == 0) return 0;

        double carbRatio = (carbohydrateG / total) * 100;
        double proteinRatio = (proteinG / total) * 100;
        double fatRatio = (fatG / total) * 100;

        boolean carbOK = (carbRatio >= CARB_MIN && carbRatio <= CARB_MAX);
        boolean proteinOK = (proteinRatio >= PROTEIN_MIN && proteinRatio <= PROTEIN_MAX);
        boolean fatOK = (fatRatio >= FAT_MIN && fatRatio <= FAT_MAX);

        int countOK = (carbOK ? 1 : 0) + (proteinOK ? 1 : 0) + (fatOK ? 1 : 0);

        if (countOK == 3) return MAX_NUTRIENT_SCORE;
        if (countOK == 2) return (int)(MAX_NUTRIENT_SCORE * 0.66);
        if (countOK == 1) return (int)(MAX_NUTRIENT_SCORE * 0.33);
        return 0;
    }


    // 나이 계산
    private int calculateAge(Date birthDate) {
        Calendar today = Calendar.getInstance();
        Calendar birthCal = Calendar.getInstance();
        birthCal.setTime(birthDate);

        int age = today.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR);

        // 생일이 아직 안 지났으면 -1
        if (today.get(Calendar.DAY_OF_YEAR) < birthCal.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age;
    }
}

