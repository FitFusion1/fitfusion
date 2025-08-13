package com.fitfusion.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * API에서 받은 Serving Size 문자열을
 * 수치(size)와 단위(unit)로 분리해서 담는 VO 클래스
 *
 * 예)
 *   - "100g" → size = 100.0, unit = "g"
 *   - "1kg"  → size = 1.0,   unit = "kg"
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServingInfo {

    /**
     * 수치 값
     * 예: 100.0
     */
    private Double size;

    /**
     * 단위 심볼
     * 예: "g", "kg", "ml"
     */
    private String unit;
}
