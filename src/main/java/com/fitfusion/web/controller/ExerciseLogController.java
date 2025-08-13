package com.fitfusion.web.controller;

import com.fitfusion.dto.RoutineLogDto;
import com.fitfusion.security.SecurityUser;
import com.fitfusion.service.ExerciseLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequiredArgsConstructor
@RequestMapping("/exerciseLog")
@PreAuthorize("isAuthenticated()")
public class ExerciseLogController {

    private final ExerciseLogService exerciseLogService;


    @PutMapping("/updateLog")
    public String updateLog(@AuthenticationPrincipal SecurityUser user,
                            @ModelAttribute RoutineLogDto routineLog,
                            @RequestParam("sessionId") int sessionId) {
        exerciseLogService.updateLog(routineLog, user.getUser().getUserId(), sessionId);
        System.out.println("UpdateLog Request Data: " + routineLog);
        return "redirect:/exerciseLog/list";
    }

    @GetMapping("/updateLog/{routineId}/{sessionId}")
    public String updateLog(@AuthenticationPrincipal SecurityUser user, @PathVariable("routineId") int routineId, @PathVariable int sessionId, Model model) {
        RoutineLogDto routineLog = exerciseLogService.getRoutineLogDetailByUserIdAndRoutineId(user.getUser().getUserId(), routineId, sessionId);
        if (!routineLog.getExerciseLogs().isEmpty()) {
            routineLog.setLogDate(routineLog.getExerciseLogs().getFirst().getLogDate());
        }

        routineLog.setSessionId(sessionId);
        model.addAttribute("routineLog", routineLog);

       return "myexercise/ExerciseLogEdit";
    }

    @GetMapping("/list")
    public String ExerciseLogPage() {
        return "myexercise/ExerciseLog";
    }


    @DeleteMapping("/deleteLog/{sessionId}")
    public String deleteExecute(@AuthenticationPrincipal SecurityUser user, @PathVariable("sessionId") int sessionId){

        exerciseLogService.deleteExerciseLog(user.getUser().getUserId(), sessionId);
        return "redirect:/exerciseLog/list";
    }
}
