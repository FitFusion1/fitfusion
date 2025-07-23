package com.fitfusion.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MealRecordController {

    @GetMapping("/meal/record")
    public String foodSearch() {
        // resources/templates/meal/meal-record.html을 렌더링
        return "meal/meal-record";
    }

}
