package com.fitfusion.enums;

public enum GoalType {
    LOSS_WEIGHT("체중 감량"),
    GAIN_WEIGHT("체중 증가"),
    MAINTAIN_WEIGHT("체중 유지"),
    GAIN_MUSCLE("근육 증가"),
    IMPROVE_HEALTH("건강한 생활습관 개선");

    private final String goalName;

    GoalType(String goalName) {
        this.goalName = goalName;
    }

    public String getgoalName() {
        return goalName;
    }
}
