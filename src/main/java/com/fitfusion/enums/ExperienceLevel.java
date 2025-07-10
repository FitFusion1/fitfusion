package com.fitfusion.enums;

import lombok.Getter;

@Getter
public enum ExperienceLevel {
    NONE("경험없음"),
    BASIC("초보자"),
    MEDIUM("중급자"),
    HIGH("고급자");

    private final String level;

    ExperienceLevel(String level) {
        this.level = level;
    }

}
