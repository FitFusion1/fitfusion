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

}
