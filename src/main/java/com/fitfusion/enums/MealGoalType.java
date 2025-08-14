package com.fitfusion.enums;

import lombok.Getter;

// 추후 추가 예정
@Getter
public enum MealGoalType {
    LOSS("loss", "감량"),
    MAINTAIN("maintain", "유지"),
    GAIN("gain", "증량");

    private final String code;      // URL이나 내부 처리용
    private final String label;     // 화면에 보여줄 한글 이름

    MealGoalType(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public static MealGoalType fromCode(String code) {
        if (code == null) {
            throw new IllegalArgumentException("Code must not be null");
        }
        for (MealGoalType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown MealGoal: " + code);
    }
}

// 추후 확장 계획
//@Getter
//public enum DietGoalType {
//    LOSS("감량", 43, 26, 31, "체중 감량 목표"),
//    GAIN("증량", 50, 20, 30, "체중 증가 목표"),
//    FAT_LOSS("체지방 감소", 43, 26, 31, "체지방 감소 목표"),
//    MUSCLE("근력 운동", 50, 26, 24, "근력 강화 목표"),
//    BLOOD_SUGAR("혈당 관리", 43, 26, 31, "혈당 조절 식단"),
//    BLOOD_PRESSURE("혈압 관리", 50, 20, 30, "혈압 조절 식단"),
//    CHOLESTEROL("콜레스테롤 관리", 50, 25, 25, "콜레스테롤 조절 식단"),
//    TRIGLYCERIDE("중성지방 관리", 50, 25, 25, "중성지방 조절 식단"),
//    GENERAL("일반", 50, 20, 30, "균형 잡힌 식단");
//
//    private final String label;
//    private final int carbRatio;
//    private final int proteinRatio;
//    private final int fatRatio;
//    private final String description;
//
//    DietGoalType(String label, int carbRatio, int proteinRatio, int fatRatio, String description) {
//        this.label = label;
//        this.carbRatio = carbRatio;
//        this.proteinRatio = proteinRatio;
//        this.fatRatio = fatRatio;
//        this.description = description;
//    }
//}