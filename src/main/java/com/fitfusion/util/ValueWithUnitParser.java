package com.fitfusion.util;

import com.fitfusion.enums.FoodUnit;
import com.fitfusion.vo.ServingInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 원본 문자열에서 "값 + 단위"를 추출하고
 * 표준화된 ServingInfo(size, unitSymbol)로 변환하는 유틸리티 클래스.
 *
 * <p>예:
 * <ul>
 *   <li>"100g" → size=100.0, unitSymbol="g"</li>
 *   <li>"1,000그램" → size=1000.0, unitSymbol="g"</li>
 *   <li>"591mL" → size=591.0, unitSymbol="ml"</li>
 * </ul>
 */
public class ValueWithUnitParser {

    private static final Logger log = LoggerFactory.getLogger(ValueWithUnitParser.class);

    /** 숫자 + 단위 추출 정규식 (예: "100g", "1,000그램", "591mL") */
    private static final Pattern PATTERN =
            Pattern.compile("([0-9.,]+)\\s*([a-zA-Z가-힣%㎍]+)");

    /**
     * raw 문자열을 파싱하여 ServingInfo로 반환합니다.
     *
     * @param raw 원본 문자열 (예: "100g", "1,000그램", "591mL")
     * @return ServingInfo(size, unitSymbol) 또는 파싱 실패 시 null
     */
    public static ServingInfo parse(String raw) {
        if (raw == null || raw.isBlank()) {
            log.warn("[단위 파싱 스킵] 입력 값 없음 (raw=null or blank)");
            return null;
        }

        Matcher matcher = PATTERN.matcher(raw.trim());
        if (!matcher.find()) {
            log.warn("[정규식 매칭 실패] 입력 값: '{}' → 숫자+단위 패턴 불일치", raw);
            return null;
        }

        try {
            // 쉼표 제거 후 Double 변환
            String numberPart = matcher.group(1).replace(",", "");
            double size = Double.parseDouble(numberPart);

            // 단위 문자열
            String rawUnit = matcher.group(2);
            FoodUnit unit = FoodUnit.fromString(rawUnit);

            // 표준화된 심볼 (enum 매핑 실패 시 raw 유지)
            String symbol = (unit != null) ? unit.getSymbol() : rawUnit;

            return new ServingInfo(size, symbol);

        } catch (NumberFormatException e) {
            log.warn("[숫자 파싱 실패] 입력 값: '{}' → '{}' 변환 불가", raw, matcher.group(1));
            return null;
        } catch (Exception e) {
            log.error("[예외 발생] 입력 값: '{}' → {}", raw, e.getMessage(), e);
            return null;
        }
    }
}
