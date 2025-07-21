package com.fitfusion.util;

import com.fitfusion.vo.ServingInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class UnitMappingUtils {

    private static final Logger log = LoggerFactory.getLogger(UnitMappingUtils.class);

    // 단위 심볼 → ID 매핑
    private static final Map<String, Integer> UNIT_MAP = Map.ofEntries(
            Map.entry("g", 1),
            Map.entry("kg", 2),
            Map.entry("ml", 3),
            Map.entry("l", 4),
            Map.entry("kcal", 5),
            Map.entry("mg", 6),
            Map.entry("㎍", 7),    // 마이크로그램
            Map.entry("개", 8),
            Map.entry("slice", 9),
            Map.entry("회", 10),
            Map.entry("iu", 11),
            Map.entry("%", 12)
    );

    // ID → 단위 심볼 역매핑
    private static final Map<Integer, String> UNIT_ID_TO_SYMBOL_MAP =
            UNIT_MAP.entrySet().stream()
                    .collect(Collectors.toUnmodifiableMap(Map.Entry::getValue, Map.Entry::getKey));

    // 단위 ID 매핑 실패 시 기본값
    private static final int DEFAULT_UNIT_ID = -1;

    /**
     * 문자열 원시값(raw)을 파싱해서 value, unitId를 각각 setter로 전달
     */
    public static void applyParsedValue(String raw,
                                        Consumer<Double> valueSetter,
                                        Consumer<Integer> unitIdSetter) {
        ServingInfo parsed = UnitValueParser.parse(raw);
        if (parsed != null) {
            valueSetter.accept(parsed.getSize());
            Integer unitId = getUnitIdBySymbol(parsed.getUnitSymbol());
            unitIdSetter.accept(unitId);
        } else {
            log.warn("단위 파싱 실패: 원시값 '{}'", raw);
        }
    }

    /**
     * 단위 심볼(String) → 단위 ID(Integer) 매핑
     */
    public static Integer getUnitIdBySymbol(String symbol) {
        if (symbol == null) return null;
        String key = symbol.trim().toLowerCase();
        Integer unitId = UNIT_MAP.get(key);
        if (unitId == null) {
            log.warn("정의되지 않은 단위 심볼 '{}'", symbol);
            return DEFAULT_UNIT_ID;  // 또는 null 반환 원하면 수정 가능
        }
        return unitId;
    }

    /**
     * 단위 ID(Integer) → 단위 심볼(String) 매핑
     */
    public static String getUnitSymbolById(Integer unitId) {
        if (unitId == null) return null;
        String symbol = UNIT_ID_TO_SYMBOL_MAP.get(unitId);
        if (symbol == null) {
            log.warn("정의되지 않은 단위 ID '{}'", unitId);
        }
        return symbol;
    }

    /**
     * 현재 등록된 모든 단위 심볼 리스트 반환 (프론트에서 드롭다운 등 활용 가능)
     */
    public static Set<String> getAllUnitSymbols() {
        return UNIT_MAP.keySet();
    }

    /**
     * 현재 등록된 모든 단위 ID 리스트 반환
     */
    public static Set<Integer> getAllUnitIds() {
        return UNIT_ID_TO_SYMBOL_MAP.keySet();
    }
}

// 개선 전 버전1. 단위(기호) -> 단위(id) 매핑 기능만 존재함.
//package com.fitfusion.util;
//
//import com.fitfusion.vo.ServingInfo;
//
//import java.util.Map;
//import java.util.function.Consumer;
//
//public class UnitMappingUtils {
//
//    // 단위 기호 → 단위 ID 매핑 (단위 ID는 DB에 등록된 값 기준)
//    private static final Map<String, Integer> UNIT_MAP = Map.ofEntries(
//            Map.entry("g", 1),
//            Map.entry("kg", 2),
//            Map.entry("ml", 3),
//            Map.entry("l", 4),
//            Map.entry("kcal", 5),
//            Map.entry("mg", 6),
//            Map.entry("㎍", 7),       // 마이크로그램(μg), 유니코드 기호
//            Map.entry("개", 8),
//            Map.entry("slice", 9),
//            Map.entry("회", 10),
//            Map.entry("iu", 11),     // 대소문자 구분 없이 처리 위해 소문자 사용
//            Map.entry("%", 12)
//    );
//
//    /**
//     * 문자열 원시값(raw)을 파싱해서 value, unitId를 각각 setter로 전달
//     */
//    public static void applyParsedValue(String raw,
//                                        Consumer<Double> valueSetter,
//                                        Consumer<Integer> unitIdSetter) {
//        ServingInfo parsed = UnitValueParser.parse(raw);
//        if (parsed != null) {
//            valueSetter.accept(parsed.getSize());
//            unitIdSetter.accept(getUnitIdBySymbol(parsed.getUnitSymbol()));
//        }
//    }
//
//    /**
//     * 단위 심볼(String) → 단위 ID(Integer) 매핑
//     */
//    public static Integer getUnitIdBySymbol(String symbol) {
//        return symbol == null ? null : UNIT_MAP.getOrDefault(symbol.trim().toLowerCase(), null);
//    }
//
//}
