package com.fitfusion.enums;

public enum AvoidReason {
    INJURY("부상"),
    AVOID("선호하지 않음");

    private final String description;

    AvoidReason(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
