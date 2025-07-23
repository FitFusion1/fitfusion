package com.fitfusion.web.controller;

import com.fitfusion.dto.ExerciseLogDto;
import com.fitfusion.service.ExerciseGoalService;
import com.fitfusion.service.ExerciseLogService;
import com.fitfusion.service.ExerciseService;
import com.fitfusion.service.RoutineService;
import com.fitfusion.vo.ExerciseLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/myexercise")
@RequiredArgsConstructor
public class MyExerciseController {

    private final ExerciseService exerciseService;
    private final ExerciseGoalService goalService;
    private final RoutineService routineService;
    private final ExerciseLogService logService;
    private final int userId = 1;
    private final ExerciseLogService exerciseLogService;

    @GetMapping("")
    public String MyExercisePage(Model model) {
        List<ExerciseLogDto> recentLogs = exerciseLogService.getExerciseLogByUserId(userId);

        model.addAttribute("goal", goalService.getSelectedGoalByUserId(userId));
        model.addAttribute("routineList", routineService.getRoutineByUserId(userId));
        model.addAttribute("recentLogs", recentLogs);

        return "myexercise/MyExerciseMain";
    }

    @GetMapping("/exerciselog")
    public String ExerciseLogPage(Model model) {
        return "myexercise/ExerciseLog";
    }

    @GetMapping("/exercisestatus")
    public String ExerciseStatusPage(Model model) {
        return "myexercise/ExerciseStatus";
    }

    @GetMapping("/exerciselogedit")
    public String ExerciseLogEditPage(Model model) {
        return "myexercise/ExerciseLogEdit";
    }


}
