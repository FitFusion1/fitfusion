package com.fitfusion.service;

import com.fitfusion.enums.DietGoalType;
import com.fitfusion.vo.User;
import org.springframework.stereotype.Service;

import java.time.Date;
import java.time.LocalDate;
import java.time.Period;
import java.util.Date;

@Service
public class MealScoreService {

    // 점수 가중치
    private static final int MAX_CALORIE_SCORE = 70;
    private static final int MAX_MACRO_SCORE = 30;

    // 탄단지 비율 기준 (한국 & AMDR 가이드라인 기반)
    private static final double CARB_MIN = 50;
    private static final double CARB_MAX = 65;
    private static final double PROTEIN_MIN = 15;
    private static final double PROTEIN_MAX = 25;
    private static final double FAT_MIN = 15;
    private static final double FAT_MAX = 25;

    // 목표 칼로리 계산(활동계수 고정)
    public double calculateGoalCalories(User user, String goalType) {
        int age = calculateAge(user.getBirthDate());

        // BMR 계산
        double bmr = (10 * user.getWeight()) + (6.25 * user.getHeight()) - (5 * age);
        bmr += user.getGender().equalsIgnoreCase("M") ? 5 : -161;

        // 활동계수 고정
        double tdee = bmr * 1.3;

        // 목표 유형별 조정
        switch (DietGoalType) {
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
    // 나이 계산
    private int calculateAge(java.util.Date birthDate) {
        java.util.Date birth = birthDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toDate();
        return Period.between(birth, LocalDate.now()).getYears();
    }

    public int calculateTotalScore(double goalCalories, double actualCalories,
                                   double carbs, double protein, double fat) {
        int calorieScore = calculateCalorieScore(goalCalories, actualCalories);
        int macroScore = calculateMacroScore(carbs, protein, fat);
        return calorieScore + macroScore;
    }

    private int calculateCalorieScore(double goalCalories, double actualCalories) {
        double diff = Math.abs(actualCalories - goalCalories);
        double tolerance = goalCalories * 0.1;
        if (diff <= tolerance) return 70;
        if (diff <= tolerance * 2) return 50;
        return 30;
    }

    private int calculateMacroScore(double carbs, double protein, double fat) {
        double total = carbs + protein + fat;
        if (total == 0) return 0;
        double carbRatio = (carbs / total) * 100;
        double proteinRatio = (protein / total) * 100;
        double fatRatio = (fat / total) * 100;

        boolean carbOK = (carbRatio >= 50 && carbRatio <= 65);
        boolean proteinOK = (proteinRatio >= 15 && proteinRatio <= 25);
        boolean fatOK = (fatRatio >= 15 && fatRatio <= 25);

        int countOK = (carbOK ? 1 : 0) + (proteinOK ? 1 : 0) + (fatOK ? 1 : 0);

        if (countOK == 3) return 30;
        else if (countOK == 2) return 20;
        else if (countOK == 1) return 10;
        else return 0;
    }
}

