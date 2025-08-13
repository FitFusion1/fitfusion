package com.fitfusion.util;

import com.fitfusion.enums.FoodUnit;
import com.fitfusion.vo.ServingInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/**
 * ValueWithUnitApplier
 * - ServingSize: applyParsedValueStrict() → 유효하지 않으면 insert 스킵
 * - foodWeight: applyParsedValue() → 값 없으면 null 저장
 */
public class ValueWithUnitApplier {

    private static final Logger log = LoggerFactory.getLogger(ValueWithUnitApplier.class);

    /**
     * ServingSize 전용 (필수 값)
     * 값 없거나 단위 이상하면 false 반환 → DB insert 스킵
     */
    public static boolean applyServingSizeValue(String raw,
                                                 Consumer<Double> valueSetter,
                                                 Consumer<FoodUnit> unitSetter) {
        if (raw == null || raw.isBlank()) {
            return false;
        }

        ServingInfo parsed = ValueWithUnitParser.parse(raw);
        if (parsed == null || parsed.getSize() == null) {
            return false;
        }

        valueSetter.accept(parsed.getSize());

        FoodUnit unit = mapToFoodUnit(parsed.getUnit());
        if (unit == null) {
            return false; // 단위 이상 → insert 스킵
        }

        unitSetter.accept(unit);
        return true;
    }

    /**
     * foodWeight 전용 (선택 값)
     * 값 없으면 null 세팅 → insert는 무조건 진행
     */
    public static void applyWeightValue(String raw,
                                        Consumer<Double> valueSetter,
                                        Consumer<FoodUnit> unitSetter) {
        if (raw == null || raw.isBlank()) {
            return; // null 그대로
        }

        ServingInfo parsed = ValueWithUnitParser.parse(raw);
        if (parsed == null || parsed.getSize() == null) {
            return;
        }

        valueSetter.accept(parsed.getSize());

        FoodUnit unit = mapToFoodUnit(parsed.getUnit());
        if (unit == null) {
            log.warn("[foodWeight] 단위 매핑 실패: raw='{}', parsed='{}' → null로 저장", raw, parsed.getUnit());
        }

        unitSetter.accept(unit); // unit=null 가능
    }

    /**
     * 단위 문자열을 Enum으로 매핑
     * g → GRAM, ml → MILLILITER, 그 외 → null
     */
    private static FoodUnit mapToFoodUnit(String unit) {
        if (unit == null) return null;
        String normalized = unit.toLowerCase();
        if (normalized.equals("g")) return FoodUnit.GRAM;
        if (normalized.equals("ml")) return FoodUnit.MILLILITER;
        return null;
    }
}


// 지금은 unitId 필요 없음. 다양한 단위 필요 없음.
//**
// * "값 + 단위" 형태의 raw 문자열을 파싱하고
// * DTO 필드에 적용하는 유틸리티 클래스.
// *
// * 예: "100g" → DTO.setValue(100.0), DTO.setUnitId(1)
// */
//public class ValueWithUnitApplier {
//
//    private static final Logger log = LoggerFactory.getLogger(ValueWithUnitApplier.class);
//
//    /**
//     * 원본 문자열(raw)을 파싱 → DTO에 값(value)과 단위 ID(unitId)를 적용
//     *
//     * 예시:
//     *   "100g"       → valueSetter(100.0), unitIdSetter(1)
//     *   "1,000그램"  → valueSetter(1000.0), unitIdSetter(1)
//     *   "591mL"      → valueSetter(591.0), unitIdSetter(3)
//     *
//     * @param raw          원본 문자열 (예: "100g", "1,000그램", "591mL")
//     * @param valueSetter  파싱된 값(Double)을 DTO에 세팅하는 Setter
//     * @param unitIdSetter 단위 ID(Integer)를 DTO에 세팅하는 Setter
//     */
//    public static void applyParsedValue(String raw,
//                                        Consumer<Double> valueSetter,
//                                        Consumer<Integer> unitIdSetter) {
//        if (raw == null || raw.isBlank()) {
//            log.warn("[단위 파싱 스킵] 입력 값 없음 (raw=null or blank)");
//            return;
//        }
//
//        ServingInfo parsed = ValueWithUnitParser.parse(raw);
//        if (parsed == null) {
//            log.warn("[단위 파싱 실패] raw='{}' → ServingInfo 변환 불가", raw);
//            return;
//        }
//
//        // 값 적용
//        valueSetter.accept(parsed.getSize());
//
//        // 단위 ID 적용 (FoodUnit Enum클래스에 단위가 많으면 필요한 로직)
//        FoodUnit unit = FoodUnit.fromString(parsed.getUnit());
//        if (unit != null) {
//            unitIdSetter.accept(unit.getId());
//        } else {
//            log.warn("[단위 매핑 실패] unit='{}' → FoodUnit enum 매칭 안 됨", parsed.getUnit());
//            unitIdSetter.accept(null);
//        }
//    }
//}