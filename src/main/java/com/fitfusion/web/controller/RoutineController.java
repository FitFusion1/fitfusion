package com.fitfusion.web.controller;

import com.fitfusion.dto.*;
import com.fitfusion.security.SecurityUser;
import com.fitfusion.service.ExerciseLogService;
import com.fitfusion.service.ExerciseService;
import com.fitfusion.service.RoutineService;
import com.fitfusion.vo.Exercise;
import com.fitfusion.vo.ExerciseLog;
import com.fitfusion.vo.Routine;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.filter.HiddenHttpMethodFilter;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/routine")
@RequiredArgsConstructor
public class RoutineController {

    private final RoutineService routineService;
    private final ExerciseLogService exerciseLogservice;
    private final ExerciseService exerciseService;


    @GetMapping("/list")
   public String createRoutine(@AuthenticationPrincipal SecurityUser user, Model model) {
        List<RoutineListDto> routines = routineService.getRoutineListByUserId(user.getUser().getUserId());
        model.addAttribute("routines", routines);
        return "routine/RoutineList";
    }

    @GetMapping("/update/{routineId}")
    public String routineUpdate(@AuthenticationPrincipal SecurityUser user, @PathVariable("routineId") int routineId, Model model) {
        RoutineDetailDto routines = routineService.getRoutineDetail(routineId, user.getUser().getUserId());
        List<Exercise> exerciseList = exerciseService.getAllExercises();
        model.addAttribute("routine", routines);
        model.addAttribute("exerciseList", exerciseList);
        return "routine/RoutineEdit";
    }

    @PutMapping("/update/custom")
    public String routineUpdate(@AuthenticationPrincipal SecurityUser user, @ModelAttribute RoutineDetailDto routine) {
        routineService.updateCustomRoutine(user.getUser().getUserId(), routine);
        return "redirect:/routine/list";
    }


    @DeleteMapping("/delete/{routineId}")
    public String routineDelete(@AuthenticationPrincipal SecurityUser user,
                                @PathVariable int routineId) {
        routineService.deleteRoutineListByUserAndRoutine(user.getUser().getUserId(), routineId);
        return "redirect:/routine/list";
    }

    @GetMapping("/detail/{routineId}")
    public String routineDetail(@AuthenticationPrincipal SecurityUser user, @PathVariable int routineId, Model model) {
        RoutineDetailDto routine = routineService.getRoutineDetail(routineId, user.getUser().getUserId());
        model.addAttribute("routine", routine);
        return "routine/RoutineDetail";
    }

    @GetMapping("/targetRecommend")
    public String targetRecommend(Model model) {
        return "routine/TargetRoutineRecommendations";
    }

}
