package com.fitfusion.util;

public class StringToDoubleParser {

    /**
     * 문자열을 Double로 변환
     * - null 또는 빈 문자열이면 null 반환
     * - 숫자, 소수점, 쉼표, - 부호 외 문자는 제거
     * - 쉼표도 제거
     * @param str 변환할 문자열
     * @return Double 값 (또는 null)
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

        return Double.parseDouble(str);
    }
}
