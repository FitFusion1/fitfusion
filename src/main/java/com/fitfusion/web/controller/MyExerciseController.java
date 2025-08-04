package com.fitfusion.web.controller;

import com.fitfusion.dto.ExerciseLogDto;
import com.fitfusion.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/myexercise")
@RequiredArgsConstructor
public class MyExerciseController {

    private final ExerciseService exerciseService;
    private final ExerciseGoalService goalService;
    private final RoutineService routineService;
    private final ExerciseLogService logService;
    private final ExerciseLogService exerciseLogService;
    private final ExerciseStatusService exerciseStatusService;
    int userId = 3;

    @GetMapping("")
    public String MyExercisePage() {
        return "myexercise/MyExerciseMain";
    }


    @GetMapping("/exercisestatus")
    public String ExerciseStatusPage(Model model) {

        Map<String, Object> status = exerciseStatusService.getExerciseStatus(userId);
        model.addAttribute("exerciseStatus", status);

        return "myexercise/ExerciseStatus";
    }


}
