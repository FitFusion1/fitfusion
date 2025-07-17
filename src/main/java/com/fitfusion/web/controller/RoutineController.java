package com.fitfusion.web.controller;

import com.fitfusion.dto.RoutineListDto;
import com.fitfusion.service.RoutineService;
import com.fitfusion.vo.Routine;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/routine")
public class RoutineController {

    private final RoutineService routineService;
    private final int userId = 1;

    public RoutineController(RoutineService routineService) {
        this.routineService = routineService;
    }


    @GetMapping("/list")
   public String createRoutine(Model model) {
        List<RoutineListDto> routines = routineService.getRoutineListByUserId(userId);
        model.addAttribute("routines", routines);
        return "routine/RoutineList";
    }

    @GetMapping("/update")
    public String routineUpdate(int routineId, Model model) {
        List<RoutineListDto> routines = routineService.getRoutineByUserId(userId, routineId);
        model.addAttribute("routines", routines);
        return "routine/RoutineEdit";
    }

    @PostMapping
    public String routineUpdate(Routine routine) {
        routine.setUserId(userId);
        routineService.updateRoutine(routine);
        return "redirect:/routine/list";
    }

    @GetMapping("/execute")
    public String routineExecute() {

        return "routine/RoutineCompletion";
    }

    @PostMapping("/delete")
    public String routineDelete(int routineId) {
        routineService.deleteRoutineListByUserAndRoutine(userId, routineId);
        return "redirect:/routine/list";
    }
}
