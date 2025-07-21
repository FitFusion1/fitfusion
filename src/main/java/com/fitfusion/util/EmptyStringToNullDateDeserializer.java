package com.fitfusion.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Jackson JSON 역직렬화 시
 * 빈 문자열("")을 null로 처리하고,
 * 유효한 날짜 문자열("yyyy-MM-dd")은 java.util.Date 객체로 변환하는 deserializer.
 *
 * 예:
 * - "" → null
 * - "2024-07-21" → Date 객체
 */
public class EmptyStringToNullDateDeserializer extends JsonDeserializer<Date> {

    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getText().trim();

        if (value.isEmpty()) {
            return null;
        }

        try {
            return formatter.parse(value);
        } catch (Exception e) {
            throw new RuntimeException("Invalid date format: " + value);
        }
    }
}

