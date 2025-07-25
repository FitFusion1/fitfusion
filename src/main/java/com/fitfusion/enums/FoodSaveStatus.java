package com.fitfusion.enums;

public enum FoodSaveStatus {
    SUCCESS,      // 저장 성공
    DUPLICATE,    // 중복으로 저장 생략
    INVALID,      // 유효성 검사 실패
    ERROR         // DB 저장 중 예외 발생
}
