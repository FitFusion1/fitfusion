package com.fitfusion.web.controller;

import org.springframework.web.bind.annotation.GetMapping;

public class MealRecordController {

    @GetMapping("/meal/record")
    public String foodSearch() {
        // resources/templates/meal-record.html을 렌더링
        return "meal-record";
    }

}
