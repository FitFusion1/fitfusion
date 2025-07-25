package com.fitfusion.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ConditionLevel {
    GOOD("좋음", "😊"),
    NORMAL("보통", "😐"),
    BAD("나쁨", "😵");

    private final String level;
    private final String emoji;

    public static ConditionLevel fromLevel(String levelName) {
        for (ConditionLevel cl : ConditionLevel.values()) {
            if (cl.getLevel().equals(levelName)) {
                return cl;
            }
        }
        throw new IllegalArgumentException("❗ ConditionLevel 매칭 실패: " + levelName);
    }

}
