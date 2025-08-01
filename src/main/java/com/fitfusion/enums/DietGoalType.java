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
