package com.fitfusion.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitfusion.dto.FoodDto;
import com.fitfusion.service.FoodService;
import groovyjarjarasm.asm.TypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/meal")
@RequiredArgsConstructor
public class MealRecordController {

    private final FoodService foodService;

    @GetMapping("/record")
    public String showMealRecordPage(@RequestParam(required = false) String keyword, Model model) {
        List<FoodDto> foods = new ArrayList<>();
        if (keyword != null && !keyword.isBlank()) {
            foods = foodService.searchFoods(keyword);
        }
        model.addAttribute("foods", foods);
        model.addAttribute("keyword", keyword);
        return "meal/meal-record";
    }

    // 선택된 음식 저장 (폼 제출)
    @PostMapping("/record/save")
    public String saveMealRecord(@RequestParam String mealTime,
                                 @RequestParam String selectedFoods) throws JsonProcessingException {
        // JSON → 객체 변환
        ObjectMapper mapper = new ObjectMapper();
        List<SelectedFoodDto> foodList = mapper.readValue(selectedFoods, new TypeReference<>() {});

        // 서비스 호출 (DB 저장)
        mealRecordService.saveMealRecord(mealTime, foodList);

        // 저장 완료 후 확인 페이지로 이동
        return "redirect:/meal/record/confirm";
    }

    // 결과 페이지
    @GetMapping("/record/confirm")
    public String showConfirmPage(Model model) {
        model.addAttribute("summary", mealRecordService.getSummary());
        return "meal/meal-record-confirm";
    }
}

//@Controller
//public class MealRecordController {
//
//
//    @GetMapping("/meal/record")
//    public String foodSearch() {
//        // resources/templates/meal/meal-record.html을 렌더링
//        return "meal/meal-record";
//    }
//}
