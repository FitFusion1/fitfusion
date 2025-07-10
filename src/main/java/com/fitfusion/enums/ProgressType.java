package com.fitfusion.enums;

public enum ProgressType {
    WEIGHT("체중", "kg", "아침 공복 체중 측정"),
    BODY_FAT("체지방률", "%", "체성분 측정기로 측정"),
    MUSCLE_MASS("골격근량", "kg", "인바디로 측정");

    private final String typeName;
    private final String unit;
    private final String description;

    ProgressType(String typeName, String unit, String description) {
        this.typeName = typeName;
        this.unit = unit;
        this.description = description;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getUnit() {
        return unit;
    }

    public String getDescription() {
        return description;
    }
}
