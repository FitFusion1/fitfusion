package com.fitfusion.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;

@Controller
public class FoodRecordResultController {

    @GetMapping("/record/foodsearch/result")
    public String showFoodRecordResult(Model model) {

        List<Map<String, Object>> foodList = new ArrayList<>();

        Map<String, Object> food1 = new HashMap<>();
        food1.put("id", 1L);
        food1.put("name", "찐고구마");
        food1.put("serving", "1개 (170g)");
        food1.put("kcal", 235);
        food1.put("amount", 100);
        foodList.add(food1);

        Map<String, Object> food2 = new HashMap<>();
        food2.put("id", 2L);
        food2.put("name", "군고구마");
        food2.put("serving", "1개 (170g)");
        food2.put("kcal", 319);
        food2.put("amount", 150);
        foodList.add(food2);

        model.addAttribute("meal", "아침");
        model.addAttribute("foodList", foodList);
        model.addAttribute("totalKcal", 235 * 100 / 100 + 319 * 150 / 100);

        return "record-foodsearch-result";
    }
}
