package com.fitfusion.web.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MealDailyLogController {

    @GetMapping("/meal/daily/log")
    public String mealDailyLog() {
        return "meal-daily-log";
    }

}

