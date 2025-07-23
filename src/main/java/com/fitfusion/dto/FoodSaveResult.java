package com.fitfusion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 음식 저장 작업 결과 통계
 *
 * API -> DB 음식 저장 시
 * 저장 결과를 출력하기 위한 DTO
 *
 * ex)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FoodSaveResult {
    private int saved;
    private int duplicated;
    private int failed;
}
