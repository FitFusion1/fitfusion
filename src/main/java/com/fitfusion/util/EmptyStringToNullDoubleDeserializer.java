package com.fitfusion.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * Jackson JSON 역직렬화 시 문자열을 Double로 변환하는 deserializer.
 *
 * - 빈 문자열("") 또는 null인 경우 → null 반환
 * - 숫자 형식 문자열인 경우 → Double로 변환
 * - 그 외의 경우는 내부 유틸에서 처리
 *
 * 예:
 *   "123.45" → 123.45
 *   ""       → null
 *   null     → null
 */
public class EmptyStringToNullDoubleDeserializer extends JsonDeserializer<Double> {

    @Override
    public Double deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String text = p.getText();
        return StringToDoubleParser.toNullableDouble(text);
    }
}

// NumberUtils 만들기 전 코드 혹시 몰라 남겨봄(OldEmptyStringToNullDoubleDeserializer)
//public class EmptyStringToNullDoubleDeserializer extends JsonDeserializer<Double> {
//
//    @Override
//    public Double deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
//        String text = p.getText();
//        if (text == null || text.isBlank()) {
//            return null;
//        }
//
//        // 숫자, 소수점, 쉼표, - 부호 외 모두 제거 (단위 제거)
//        text = text.replaceAll("[^0-9.,-]", "");
//
//        if (text.isBlank()) {
//            return null;
//        }
//
//        // 쉼표 제거
//        text = text.replace(",", "");
//
//        return Double.parseDouble(text);
//    }
//}
