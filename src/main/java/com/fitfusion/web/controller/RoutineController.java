package com.fitfusion.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/routine")
public class RoutineController {

    @GetMapping("/create")
   public String createRoutine() {
        return "routine/RoutineCompletion";
    }
}
