package com.fitfusion.util;

import com.fitfusion.vo.ServingInfo;

public class ServingSizeParser {

    /**
     * ex) "100g" → size=100.0, unitSymbol="g"
     *     "1kg"  → size=1.0, unitSymbol="kg"
     *     "100"  → size=100.0, unitSymbol=null
     *     null/빈값 → null
     */
    public static ServingInfo parse(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        String numStr = value.replaceAll("[^\\d.]", "");
        String unit = value.replaceAll("[\\d.]", "").trim();

        Double size = null;
        if (!numStr.isEmpty()) {
            try {
                size = Double.parseDouble(numStr);
            } catch (NumberFormatException e) {
                // 로깅하거나 무시
                return null;
            }
        }

        if (size == null && unit.isEmpty()) {
            return null;
        }

        return new ServingInfo(size, unit.isEmpty() ? null : unit);
    }
}
