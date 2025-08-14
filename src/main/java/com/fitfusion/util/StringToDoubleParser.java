package com.fitfusion.util;

/**
 * 문자열을 Double 타입으로 변환하는 유틸리티 클래스.
 * <p>
 * 주로 JSON 역직렬화 또는 API로부터 수신한 원시 문자열("100g", "1,234.56mg") 등에서
 * 숫자만 추출해 Double로 변환할 때 사용됩니다.
 */
public class StringToDoubleParser {

    /**
     * 문자열을 Double로 변환합니다.
     *
     * 처리 규칙:
     * - null 또는 공백 문자열이면 null 반환
     * - 숫자, 소수점(.), 쉼표(,), 음수 기호(-) 외의 모든 문자 제거 (단위 제거 목적)
     * - 쉼표(,)는 제거 후 변환 (ex. "1,000.5" → 1000.5)
     * - 제거 후에도 유효 숫자가 없으면 null 반환
     * - 그 외의 경우는 Double.parseDouble()으로 변환
     *
     * 예시:
     * - "123.45g"      → 123.45
     * - "  -1,234mg"   → -1234.0
     * - "abc"          → null
     * - ""             → null
     * - null           → null
     *
     * @param str 변환할 문자열
     * @return Double 값 또는 null
     * @throws NumberFormatException 유효한 숫자 형식이 아닐 경우 (이론상 발생 가능성 낮음)
     */
    public static Double toNullableDouble(String str) {
        if (str == null || str.isBlank()) {
            return null;
        }

        // 숫자, 소수점, 쉼표, - 부호 외 모두 제거 (단위 제거 목적)
        str = str.replaceAll("[^0-9.,-]", "");

        if (str.isBlank()) {
            return null;
        }

        // 쉼표 제거
        str = str.replace(",", "");

        // 문자열을 Double로 변환
        return Double.parseDouble(str);
    }
}
