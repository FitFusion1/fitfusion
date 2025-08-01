package com.fitfusion.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PoseController {

    @GetMapping("/pose/{exerciseName}")
    public String pose(@PathVariable String exerciseName) {
        return "pose-checker/" + exerciseName;
    }
}
