package com.fitfusion.dto;

import lombok.Data;

@Data

//사용자가 선택한 음식 목록을 컨펌 화면에 보여주기 위한 DTO. (뷰 & 계산용) (DB 매핑 없음 → 컨펌 화면과 영양소 계산에서만 사용.)
public class SelectedFoodDto {
    private int foodId;       // 음식 ID
    private String foodName;  // 음식 이름
    private String makerName; // 제조사명

    private double servingSizeValue; // ex) 100
    private String unit;       // 단위 심볼 (ENUM 변환 후 UI 표시용)
    private double userIntake;       // 사용자가 입력한 섭취량

    private double calories;         // 칼로리
    private double carbohydrateG;    // 탄수화물
    private double proteinG;         // 단백질
    private double fatG;             // 지방

   private String imageUrl;          // 음식 이미지 URL (아직 못 구함)
}
