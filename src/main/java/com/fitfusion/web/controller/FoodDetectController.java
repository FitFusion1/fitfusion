package com.fitfusion.web.controller;

import com.fitfusion.service.FoodDetectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/food-ai")
public class FoodDetectController {

    @Autowired
    private FoodDetectService foodDetectService;

    @GetMapping("/upload")
    public String uploadPage() {
        return "food-ai/food-upload";  // templates/food-upload.html
    }

    @GetMapping("/upload1")
    public String uploadPageTest() {
        return "food-ai/food-upload-test";  // templates/food-upload.html
    }
}
