package com.fitfusion.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

//JSON 역직렬화 시 자동으로 숫자 파싱(JSON → Double 변환)

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
