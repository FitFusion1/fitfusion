package com.fitfusion.util;

import com.fitfusion.vo.ServingInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 숫자 + 단위 파싱 유틸
 * 예: "100g" → size=100.0, unit="g"
 *
 *  단순히 숫자와 단위를 **그대로 추출**하는 역할만 담당.
 *  단위 검증(g, mL 등)은 하지 않음 → 유효성 검사는 Applier에서 처리.
 */
public class ValueWithUnitParser {

    private static final Logger log = LoggerFactory.getLogger(ValueWithUnitParser.class);

    /** 숫자 + 단위 추출 정규식 */
    private static final Pattern PATTERN =
            Pattern.compile("([0-9.,]+)\\s*([a-zA-Z가-힣%㎍]+)?");

    /**
     * parse 메서드
     * raw 문자열을 숫자와 단위로 분리하여 ServingInfo로 반환.
     *
     * @param raw 원본 문자열 (예: "100g", "1,000그램")
     * @return ServingInfo(size, unit) or null (파싱 실패 시)
     */
    public static ServingInfo parse(String raw) {
        // 1. 입력값 검증 (null or 공백이면 파싱 불가)
        if (raw == null || raw.isBlank()) {
            return null; // STRICT 모드에서 insert skip
        }

        // 2. 불필요한 괄호 제거, 공백 제거 (예: "(100g)" → "100g")
        String normalized = raw.replaceAll("[()]", "").trim();

        // 3. 정규식 매칭 시도
        Matcher matcher = PATTERN.matcher(normalized);

        if (!matcher.find()) {
            // 매칭 실패 → 숫자+단위 패턴이 아님
            return null;
        }

        try {
            /**
             * 4. 숫자 부분 처리
             * matcher.group(1) → 첫 번째 그룹 (숫자 부분)
             * 쉼표 제거 후 Double로 변환
             * 예: "1,000" → "1000" → 1000.0
             */
            String numberPart = matcher.group(1).replace(",", "");
            Double size = Double.parseDouble(numberPart);

            /**
             * 5. 단위 부분 처리
             * matcher.group(2) → 두 번째 그룹 (단위 문자열)
             * 단위가 없는 경우 null
             * 예: "100g" → "g"
             * 예: "1,000그램" → "그램"
             */
            String unit = matcher.group(2) != null ? matcher.group(2) : null;

            /**
             * 6. ServingInfo 생성
             * size: Double 값
             * unit: raw에서 추출한 단위 문자열 그대로
             * (표준화는 Applier 단계에서 처리)
             */
            return new ServingInfo(size, unit);

        } catch (NumberFormatException e) {
            // 숫자 변환 실패 (예: "abcg" → "abc"는 숫자 아님)
            log.warn("[ValueWithUnitParser] 숫자 파싱 실패: raw='{}', error={}", raw, e.getMessage());
            return null;
        } catch (Exception e) {
            // 그 외 예외 (예: NullPointerException 등)
            log.error("[ValueWithUnitParser] 예외 발생: raw='{}', error={}", raw, e.getMessage(), e);
            return null;
        }
    }
}

//**
// * 원본 문자열에서 "값 + 단위"를 추출하고
// * 표준화된 ServingInfo(size, unit)로 변환하는 유틸리티 클래스.
// *
// * 예:
// *   "100g" → size=100.0, unit="g"
// *   "1,000그램" → size=1000.0, unit="g"
// *   "591mL" → size=591.0, unit="ml"
// */
//class ValueWithUnitParser {
//
//    private static final Logger log = LoggerFactory.getLogger(ValueWithUnitParser.class);
//
//    /** 숫자 + 단위 추출 정규식 (예: "100g", "1,000그램", "591mL") */
//    private static final Pattern PATTERN =
//            Pattern.compile("([0-9.,]+)\\s*([a-zA-Z가-힣%㎍]+)");
//
//    /**
//     * raw 문자열을 파싱하여 ServingInfo로 반환합니다.
//     *
//     * @param raw 원본 문자열 (예: "100g", "1,000그램", "591mL")
//     * @return ServingInfo(size, unit) 또는 파싱 실패 시 null
//     */
//    public static ServingInfo parse(String raw) {
//        if (raw == null || raw.isBlank()) {
//            log.warn("[단위 파싱 스킵] 입력 값 없음 (raw=null or blank)");
//            return null;
//        }
//
//        Matcher matcher = PATTERN.matcher(raw.trim());
//        if (!matcher.find()) {
//            log.warn("[정규식 매칭 실패] 입력 값: '{}' → 숫자+단위 패턴 불일치", raw);
//            return null;
//        }
//
//        try {
//            // 쉼표 제거 후 Double 변환
//            String numberPart = matcher.group(1).replace(",", "");
//            double size = Double.parseDouble(numberPart);
//
//            // 단위 문자열 (FoodUnit Enum클래스의 단위가 다양할 때 필요한 로직)
//            String rawUnit = matcher.group(2);
//            FoodUnit unit = FoodUnit.fromString(rawUnit);
//
//            // 표준화된 심볼 (enum 매핑 실패 시 raw 유지)
//            String symbol = (unit != null) ? unit.getSymbol() : rawUnit;
//
//            return new ServingInfo(size, symbol);
//
//        } catch (NumberFormatException e) {
//            log.warn("[숫자 파싱 실패] 입력 값: '{}' → '{}' 변환 불가", raw, matcher.group(1));
//            return null;
//        } catch (Exception e) {
//            log.error("[예외 발생] 입력 값: '{}' → {}", raw, e.getMessage(), e);
//            return null;
//        }
//    }
//}
