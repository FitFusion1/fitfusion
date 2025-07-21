package com.fitfusion.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "home";
    }

    // 임시로 만듦
    @GetMapping("/pose")
    public String pose() {
        return "pose-checker/bicep_curl_pose_feedback";
    }
}
