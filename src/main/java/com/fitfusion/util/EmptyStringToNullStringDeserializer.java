package com.fitfusion.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * Jackson JSON 역직렬화 시, 빈 문자열("") 또는 공백 문자열을 null로 변환하는 deserializer.
 *
 * 사용 예:
 * - ""         → null
 * - "   "      → null
 * - "content" → "content"
 *
 * 주로 선택적 필드에서 빈 문자열을 null로 처리하고자 할 때 사용.
 */
public class EmptyStringToNullStringDeserializer extends JsonDeserializer<String> {
    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String text = p.getText();
        return (text == null || text.trim().isEmpty()) ? null : text;
    }
}