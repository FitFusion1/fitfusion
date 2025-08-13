package com.fitfusion.enums;

import lombok.Getter;

// 추후 추가 예정
@Getter
public enum DietGoalType {
    LOSS("감량"),
    MAINTAIN("유지"),
    GAIN("증량");

    private final String label;

    DietGoalType(String label) {
        this.label = label;
    }

    public static DietGoalType fromLabel(String label) {
        for (DietGoalType type : values()) {
            if (type.label.equals(label)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown diet goal: " + label);
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