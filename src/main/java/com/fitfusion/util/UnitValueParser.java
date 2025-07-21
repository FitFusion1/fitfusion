package com.fitfusion.util;

import com.fitfusion.vo.ServingInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnitValueParser {

    /**
     * Api에서 받아온 ServingSize(문자열) 100g을 100과 g으로 나눔
     * 예:
     *   "100g" → size=100.0, unitSymbol="g"
     *   "1kg" → size=1.0, unitSymbol="kg"
     *   "1,000g" → size=1000.0, unitSymbol="g"
     *   "100" → size=100.0, unitSymbol=null
     *   null / 빈 값 → null
     */
    private static final Logger log = LoggerFactory.getLogger(UnitValueParser.class);

    private static final Pattern VALUE_WITH_UNIT_PATTERN =
            Pattern.compile("([0-9.,]+)\\s*([a-zA-Z㎍%가-힣]+)");

    public static ServingInfo parse(String raw) {
        if (raw == null || raw.isBlank()) return null;

        Matcher matcher = VALUE_WITH_UNIT_PATTERN.matcher(raw.trim());
        if (matcher.find()) {  // 변경 포인트
            try {
                double size = Double.parseDouble(matcher.group(1).replace(",", ""));
                String symbol = matcher.group(2);
                return new ServingInfo(size, symbol);
            } catch (NumberFormatException e) {
                log.warn("숫자 파싱 실패: '{}'", raw);
            }
        } else {
            log.warn("정규식 매칭 실패: '{}'", raw);
        }

        return null;
    }


//    // 개선 전 버전1(클래스명 : ServingSizeParser)
//    /**
//     * ex) "100g" → size=100.0, unitSymbol="g"
//     *     "1kg"  → size=1.0, unitSymbol="kg"
//     *     "100"  → size=100.0, unitSymbol=null
//     *     null/빈값 → null
//     */
//    public static ServingInfo parse(String value) {
//        if (value == null || value.trim().isEmpty()) {
//            return null;
//        }
//
//        String numStr = value.replaceAll("[^\\d.]", "");
//        String unit = value.replaceAll("[\\d.]", "").trim();
//
//        Double size = null;
//        if (!numStr.isEmpty()) {
//            try {
//                size = Double.parseDouble(numStr);
//            } catch (NumberFormatException e) {
//                // 로깅하거나 무시
//                return null;
//            }
//        }
//
//        if (size == null && unit.isEmpty()) {
//            return null;
//        }
//
//        return new ServingInfo(size, unit.isEmpty() ? null : unit);
//    }


//    개선 전 버전2(클래스명: ValueWithUnitParser) (1,000g 같은 , 붙은 값 처리 못함)
//    public static ServingInfo parse(String value) {
//        if (value == null || value.isBlank()) return null;
//
//        String trimmed = value.trim();
//        StringBuilder numberPart = new StringBuilder();
//        StringBuilder unitPart = new StringBuilder();
//
//        for (char c : trimmed.toCharArray()) {
//            if (Character.isDigit(c) || c == '.') {
//                numberPart.append(c);
//            } else {
//                unitPart.append(c);
//            }
//        }
//
//        if (numberPart.length() == 0 && unitPart.length() == 0) return null;
//
//        Double size = null;
//        try {
//            if (numberPart.length() > 0) {
//                size = Double.parseDouble(numberPart.toString());
//            }
//        } catch (NumberFormatException ignored) {}
//
//        String unit = unitPart.toString().trim();
//        return new ServingInfo(size, unit.isEmpty() ? null : unit);
//    }

//    // 개선 전 버전3. foodWeight에 null이 들어가는 경우가 있어서 버림.
//public static ServingInfo parse(String value) {
//    if (value == null || value.isBlank()) return null;
//
//    String trimmed = value.trim();
//    StringBuilder numberPart = new StringBuilder();
//    StringBuilder unitPart = new StringBuilder();
//
//    for (char c : trimmed.toCharArray()) {
//        if (Character.isDigit(c) || c == '.') {
//            numberPart.append(c);
//        } else if (c == ',') {
//            // 쉼표는 숫자에서 제거
//            continue;
//        } else {
//            unitPart.append(c);
//        }
//    }
//
//    if (numberPart.length() == 0 && unitPart.length() == 0) return null;
//
//    Double size = null;
//    try {
//        if (numberPart.length() > 0) {
//            size = Double.parseDouble(numberPart.toString());
//        }
//    } catch (NumberFormatException ignored) {}
//
//    String unit = unitPart.toString().trim();
//    return new ServingInfo(size, unit.isEmpty() ? null : unit);
//}

}
