package com.fitfusion.web.controller;

import com.fitfusion.dto.ExerciseItemDto;
import com.fitfusion.dto.ExerciseLogDto;
import com.fitfusion.dto.RoutineDetailDto;
import com.fitfusion.dto.RoutineLogDto;
import com.fitfusion.security.SecurityUser;
import com.fitfusion.service.ExerciseLogService;
import com.fitfusion.service.ExerciseService;
import com.fitfusion.service.RoutineService;
import com.fitfusion.vo.Exercise;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/exerciseLog")
public class ExerciseLogController {

    private final RoutineService routineService;
    private final ExerciseLogService exerciseLogService;
    private final ExerciseService exerciseService;


    @PutMapping("/updateLog")
    public String updateLog(@AuthenticationPrincipal SecurityUser user,
                            @ModelAttribute RoutineLogDto routineLog,
                            @RequestParam("sessionId") int sessionId) {
        exerciseLogService.updateLog(routineLog, user.getUser().getUserId());
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
    public String ExerciseLogPage(@AuthenticationPrincipal SecurityUser user, Model model) {
        List<RoutineLogDto> routineLogs = exerciseLogService.getRoutineLogsByUserId(user.getUser().getUserId());
        model.addAttribute("routineLogs", routineLogs);
        return "myexercise/ExerciseLog";
    }

    @GetMapping("/execute/{routineId}")
    public String routineExecute(@AuthenticationPrincipal SecurityUser user, @PathVariable("routineId") int routineId, Model model) {

        RoutineDetailDto routine = routineService.getRoutineDetail(routineId, user.getUser().getUserId());

        RoutineLogDto routineLogDto = new RoutineLogDto();
        List<ExerciseLogDto> exerciseLogs = new ArrayList<>();

        for (ExerciseItemDto ex : routine.getExercises()) {
            ExerciseLogDto logDto = new ExerciseLogDto();
            logDto.setExerciseId(ex.getExerciseId());
            logDto.setRoutineId(routineId);
            logDto.setUserId(user.getUser().getUserId());
            logDto.setRoutineExerciseId(ex.getRoutineExerciseId());
            logDto.setRecommendedSets(ex.getSets());
            logDto.setRecommendedReps(ex.getReps());
            exerciseLogs.add(logDto);
        }
        routineLogDto.setExerciseLogs(exerciseLogs);

        model.addAttribute("routine", routine);
        model.addAttribute("routineLogDto", routineLogDto);

        return "/routine/RoutineExcute";
    }


    @PostMapping("/execute/{routineId}")
    public String saveExecute(@AuthenticationPrincipal SecurityUser user,
                              @PathVariable("routineId") int routineId,
                              @ModelAttribute("routineLogDto") RoutineLogDto routineDto,
                              BindingResult bindingResult,
                              Model model) {


        if (bindingResult.hasErrors()) {
            RoutineDetailDto routine = routineService.getRoutineDetail(routineId, user.getUser().getUserId());
            model.addAttribute("routine", routine);
            model.addAttribute("routineLogDto", routineDto);
            return "/routine/RoutineExcute";
        }
        exerciseLogService.saveRoutineLog(user.getUser().getUserId(), routineDto);

        return "redirect:/exerciseLog/list";
    }

    @DeleteMapping("/deleteLog/{sessionId}")
    public String deleteExecute(@AuthenticationPrincipal SecurityUser user, @PathVariable("sessionId") int sessionId){

        exerciseLogService.deleteExerciseLog(user.getUser().getUserId(), sessionId);
        return "redirect:/exerciseLog/list";
    }
}
