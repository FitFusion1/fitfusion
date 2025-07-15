package com.fitfusion.web.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RecordFoodSearchController {

    @GetMapping("/record/foodsearch")
    public String foodSearch() {
        // resources/templates/record-foodsearch.html을 렌더링
        return "record-food-search";
    }
}
