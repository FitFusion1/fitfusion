package com.fitfusion.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MapPageController {

    // 기본 경로 접근
    @GetMapping("/map")
    public String map() {
        return "map";
    }

    // 기본 경로 접근
    @GetMapping("/detail")
    public String detail() {
        return "detail";
    }


    @GetMapping("/compare")
    public String showComparePage() {
        return "recentGyms";
    }
}
