package com.fitfusion.enums;

public enum BodyPart {
    CHEST("가슴"),
    BACK("등"),
    SHOULDERS("어깨"),
    LEGS("하체"),
    ARMS("팔"),
    CORE("복근");

    private final String bodyPartName;

    BodyPart(String bodyPartName) {
        this.bodyPartName = bodyPartName;
    }

    public String getBodyName() {
        return bodyPartName;
    }
}
