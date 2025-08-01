package com.fitfusion.enums;

import lombok.Getter;

import java.util.*;

@Getter
public enum FoodUnit {

    // Weight
    GRAM(1, "g", "그램", "gram", "g", "G"),
    KILOGRAM(2, "kg", "킬로그램", "kilogram", "KG"),

    // Volume
    MILLILITER(3, "ml", "밀리리터", "milliliter", "mL", "ML"),
    LITER(4, "l", "리터", "liter", "L"),

    // Energy
    KCAL(5, "kcal", "kilocalorie", "KCAL"),

    // Micro units
    MILLIGRAM(6, "mg", "밀리그램", "milligram", "MG"),
    MICROGRAM(7, "㎍", "ug", "microgram", "마이크로그램"),

    // Count
    PIECE(8, "개", "piece"),
    SLICE(9, "slice", "조각"),

    SERVING(10, "회", "serving"),

    // IU
    IU(11, "IU", "iu", "국제단위"),

    // Percentage
    PERCENT(12, "%", "percent", "퍼센트");

    private final int id;
    private final String symbol; // 표준 심볼
    private final Set<String> aliases;

    FoodUnit(int id, String symbol, String... aliases) {
        this.id = id;
        this.symbol = symbol;
        this.aliases = new HashSet<>();
        this.aliases.add(symbol.toLowerCase());
        for (String alias : aliases) {
            this.aliases.add(alias.toLowerCase());
        }
    }

    public static FoodUnit fromString(String input) {
        if (input == null || input.isBlank()) return null;
        String normalized = input.trim().toLowerCase();
        for (FoodUnit unit : values()) {
            if (unit.aliases.contains(normalized)) {
                return unit;
            }
        }
        return null;
    }

    public static FoodUnit fromId(int id) {
        for (FoodUnit unit : values()) {
            if (unit.id == id) return unit;
        }
        return null;
    }
}
