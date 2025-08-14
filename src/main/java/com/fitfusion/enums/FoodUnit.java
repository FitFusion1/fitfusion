package com.fitfusion.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.*;

public enum FoodUnit {

    G("g"),
    g("g"),
    GRAM("g"),
    mL("mL"),
    MILLILITER("mL");

    private final String symbol;

    FoodUnit(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    // DB에서 symbol을 읽어와 Enum으로 변환할 때 사용할 메소드
    public static FoodUnit fromSymbol(String symbol) {
        for (FoodUnit unit : FoodUnit.values()) {
            if (unit.getSymbol().equalsIgnoreCase(symbol)) {
                return unit;
            }
        }
        throw new IllegalArgumentException("Unknown FoodUnit symbol: " + symbol);
    }

    // 항상 symbol을 반환하도록 오버라이드
    @Override
    public String toString() {
        return symbol;
    }
}


// 버전2. TypeHandler 적용 전
//@Getter
//public enum FoodUnit {
//
//    GRAM("g"),
//    MILLILITER("mL");
//
//    private final String symbol;
//
//    FoodUnit(String symbol) {
//        this.symbol = symbol;
//    }
//}

//    버전1.
//    공공API, 사용자의 단위 입력이 표준화 되어 있는데 'alias'는 오버인 듯
//    '다양한 단위'는 음식 카테고리에 따라 드롭박스를 제공하면 쓸 수 있을 듯
//    @Getter
//    public enum FoodUnit {
//    // Weight
//    GRAM("g", "그램", "gram", "g", "G"),
//    KILOGRAM("kg", "킬로그램", "kilogram", "KG"),
//
//    // Volume
//    MILLILITER("ml", "밀리리터", "milliliter", "mL", "ML"),
//    LITER("l", "리터", "liter", "L"),
//
//    // Energy
//    KCAL("kcal", "kilocalorie", "KCAL"),
//
//    // Micro units
//    MILLIGRAM("mg", "밀리그램", "milligram", "MG"),
//    MICROGRAM("㎍", "ug", "microgram", "마이크로그램"),
//
//    // Count
//    PIECE("개", "piece"),
//    SLICE("slice", "조각"),
//
//    SERVING("회", "serving"),
//
//    // IU
//    IU("IU", "iu", "국제단위"),
//
//    // Percentage
//    PERCENT("%", "percent", "퍼센트");
//
//    private final String symbol; // 표준 심볼
//    private final Set<String> aliases;
//
//    FoodUnit(String symbol, String... aliases) {
//
//        this.symbol = symbol;
//        this.aliases = new HashSet<>();
//        this.aliases.add(symbol.toLowerCase());
//        for (String alias : aliases) {
//            this.aliases.add(alias.toLowerCase());
//        }
//    }
//
//    public static FoodUnit fromString(String input) {
//        if (input == null || input.isBlank()) return null;
//        String normalized = input.trim().toLowerCase();
//        for (FoodUnit unit : values()) {
//            if (unit.aliases.contains(normalized)) {
//                return unit;
//            }
//        }
//        return null;
//    }
