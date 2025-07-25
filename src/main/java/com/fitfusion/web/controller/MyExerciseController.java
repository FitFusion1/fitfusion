package com.fitfusion.web.controller;

import com.fitfusion.dto.ExerciseLogDto;
import com.fitfusion.dto.RoutineLogDto;
import com.fitfusion.security.SecurityUser;
import com.fitfusion.service.ExerciseGoalService;
import com.fitfusion.service.ExerciseLogService;
import com.fitfusion.service.ExerciseService;
import com.fitfusion.service.RoutineService;
import com.fitfusion.vo.ExerciseLog;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.SimpleDateFormat;
import java.util.List;

@Controller
@RequestMapping("/myexercise")
@RequiredArgsConstructor
public class MyExerciseController {

    private final ExerciseService exerciseService;
    private final ExerciseGoalService goalService;
    private final RoutineService routineService;
    private final ExerciseLogService logService;
    private final ExerciseLogService exerciseLogService;

    @GetMapping("")
    public String MyExercisePage(@AuthenticationPrincipal SecurityUser user, Model model) {
        List<ExerciseLogDto> recentLogs = exerciseLogService.getExerciseLogByUserId(user.getUser().getUserId());

        model.addAttribute("goal", goalService.getSelectedGoalByUserId(user.getUser().getUserId()));
        model.addAttribute("routineList", routineService.getRoutineByUserId(user.getUser().getUserId()));
        model.addAttribute("recentLogs", recentLogs);

        return "myexercise/MyExerciseMain";
    }

    @GetMapping("/exerciselog")
    public String ExerciseLogPage(@AuthenticationPrincipal SecurityUser user, Model model) {
        List<RoutineLogDto> routineLogs = exerciseLogService.getRoutineLogByUserId(user.getUser().getUserId());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (RoutineLogDto log : routineLogs) {
            if (log.getLogDate() != null) {
                log.setFormattedDate(sdf.format(log.getLogDate()));
            }
        }
        model.addAttribute("routineLogs", routineLogs);
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
