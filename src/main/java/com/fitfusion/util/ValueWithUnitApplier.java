package com.fitfusion.util;

import com.fitfusion.enums.FoodUnit;
import com.fitfusion.vo.ServingInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/**
 * "값 + 단위" 형태의 raw 문자열을 파싱하고
 * DTO 필드에 적용하는 유틸리티 클래스.
 *
 * 예: "100g" → DTO.setValue(100.0), DTO.setUnitId(1)
 */
public class ValueWithUnitApplier {

    private static final Logger log = LoggerFactory.getLogger(ValueWithUnitApplier.class);

    /**
     * 원본 문자열(raw)을 파싱 → DTO에 값(value)과 단위 ID(unitId)를 적용
     *
     * 예시:
     *   "100g"       → valueSetter(100.0), unitIdSetter(1)</li>
     *   "1,000그램"  → valueSetter(1000.0), unitIdSetter(1)</li>
     *   "591mL"      → valueSetter(591.0), unitIdSetter(3)</li>
     *
     * @param raw          원본 문자열 (예: "100g", "1,000그램", "591mL")
     * @param valueSetter  파싱된 값(Double)을 DTO에 세팅하는 Setter
     * @param unitIdSetter 단위 ID(Integer)를 DTO에 세팅하는 Setter
     */
    public static void applyParsedValue(String raw,
                                        Consumer<Double> valueSetter,
                                        Consumer<Integer> unitIdSetter) {
        if (raw == null || raw.isBlank()) {
            log.warn("[단위 파싱 스킵] 입력 값 없음 (raw=null or blank)");
            return;
        }

        ServingInfo parsed = ValueWithUnitParser.parse(raw);
        if (parsed == null) {
            log.warn("[단위 파싱 실패] raw='{}' → ServingInfo 변환 불가", raw);
            return;
        }

        // 값 적용
        valueSetter.accept(parsed.getSize());

        // 단위 ID 적용
        FoodUnit unit = FoodUnit.fromString(parsed.getUnitSymbol());
        if (unit != null) {
            unitIdSetter.accept(unit.getId());
        } else {
            log.warn("[단위 매핑 실패] unit='{}' → FoodUnit enum 매칭 안 됨", parsed.getUnitSymbol());
            unitIdSetter.accept(null);
        }
    }
}