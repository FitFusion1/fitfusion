package com.fitfusion.util;

import com.fitfusion.vo.ServingInfo;

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
    public static ServingInfo parse(String value) {
        if (value == null || value.isBlank()) return null;

        String trimmed = value.trim();
        StringBuilder numberPart = new StringBuilder();
        StringBuilder unitPart = new StringBuilder();

        for (char c : trimmed.toCharArray()) {
            if (Character.isDigit(c) || c == '.') {
                numberPart.append(c);
            } else if (c == ',') {
                // 쉼표는 숫자에서 제거
                continue;
            } else {
                unitPart.append(c);
            }
        }

        if (numberPart.length() == 0 && unitPart.length() == 0) return null;

        Double size = null;
        try {
            if (numberPart.length() > 0) {
                size = Double.parseDouble(numberPart.toString());
            }
        } catch (NumberFormatException ignored) {}

        String unit = unitPart.toString().trim();
        return new ServingInfo(size, unit.isEmpty() ? null : unit);
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

}
