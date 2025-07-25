package com.fitfusion.web.controller;

import com.fitfusion.dto.ExerciseLogDto;
import com.fitfusion.dto.RoutineDetailDto;
import com.fitfusion.dto.RoutineListDto;
import com.fitfusion.dto.RoutineLogDto;
import com.fitfusion.service.ExerciseLogService;
import com.fitfusion.service.ExerciseService;
import com.fitfusion.service.RoutineService;
import com.fitfusion.vo.Exercise;
import com.fitfusion.vo.ExerciseLog;
import com.fitfusion.vo.Routine;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    private final int userId = 1;
    private final ExerciseService exerciseService;


    @GetMapping("/list")
   public String createRoutine(Model model) {
        List<RoutineListDto> routines = routineService.getRoutineListByUserId(userId);
        model.addAttribute("routines", routines);
        return "routine/RoutineList";
    }

    @GetMapping("/update/{routineId}")
    public String routineUpdate(@PathVariable("routineId") int routineId, Model model) {
        RoutineDetailDto routines = routineService.getRoutineDetail(routineId);
        List<Exercise> exerciseList = exerciseService.getAllExercises();
        model.addAttribute("routine", routines);
        model.addAttribute("exerciseList", exerciseList);
        return "routine/RoutineEdit";
    }

    @PutMapping("/update/custom")
    public String routineUpdate(@ModelAttribute RoutineDetailDto routine) {
        routineService.updateCustomRoutine(userId, routine);
        return "redirect:/routine/list";
    }

    @GetMapping("/execute/{routineId}")
    public String routineExecute(@PathVariable("routineId") int routineId, Model model) {
        RoutineDetailDto routine = routineService.getRoutineDetail(routineId);
        model.addAttribute("routine", routine);
        model.addAttribute("routineLogDto", new RoutineLogDto());
        return "/routine/RoutineExcute";
    }

    @PostMapping("/execute/{routineId}")
    public String saveExecute(@PathVariable("routineId") int routineId,
                              @ModelAttribute("routineLogDto") RoutineLogDto routineDto,
                              BindingResult bindingResult,
                              HttpSession session,
                              Model model) {

        List<Integer> partialErrorIndexes = new ArrayList<>();

        for (int i = 0; i < routineDto.getExerciseLogs().size(); i++) {
            ExerciseLogDto dto = routineDto.getExerciseLogs().get(i);

            boolean setsOnly = dto.getSets() != null && dto.getReps() == null;
            boolean repsOnly = dto.getSets() == null && dto.getReps() != null;

            if (setsOnly) {
                bindingResult.rejectValue("exerciseLogs[" + i + "].reps", "required.reps", "반복 수를 입력해주세요.");
                partialErrorIndexes.add(i);
            } else if (repsOnly) {
                bindingResult.rejectValue("exerciseLogs[" + i + "].sets", "required.sets", "세트 수를 입력해주세요.");
                partialErrorIndexes.add(i);
            }
        }

        model.addAttribute("partialErrorIndexes", partialErrorIndexes);

        if (bindingResult.hasErrors()) {
            RoutineDetailDto routine = routineService.getRoutineDetail(routineId);
            model.addAttribute("routine", routine);
            model.addAttribute("routineLogDto", routineDto);
            return "/routine/RoutineExcute";
        }

        for (ExerciseLogDto dto : routineDto.getExerciseLogs()) {
            ExerciseLog log = new ExerciseLog();

            log.setUserId(userId);
            log.setRoutineExerciseId(dto.getRoutineExerciseId()); // 버그 수정: exerciseId → routineExerciseId
            log.setExerciseId(dto.getExerciseId());

            Integer sets = dto.getSets();
            Integer reps = dto.getReps();
            Integer duration = dto.getDurationMinutes();

            boolean hasPartialInput = (sets != null && sets > 0) || (reps != null && reps > 0);

            if (hasPartialInput) {
                log.setSets(sets != null ? sets : 0);
                log.setReps(reps != null ? reps : 0);
                log.setIsChecked("Y");
            } else if ("Y".equals(dto.getIsChecked())) {
                log.setSets(dto.getRecommendedSets() != null ? dto.getRecommendedSets() : 0);
                log.setReps(dto.getRecommendedReps() != null ? dto.getRecommendedReps() : 0);
                log.setIsChecked("Y");
            } else {
                log.setSets(0);
                log.setReps(0);
                log.setIsChecked("N");
            }

            log.setDurationMinutes(duration != null ? duration : 0);

            exerciseLogservice.saveExerciseLog(log);
        }

        return "redirect:/myexercise/exerciselog";
    }


    @DeleteMapping("/delete/{routineId}")
    public String routineDelete(@PathVariable int routineId) {
        routineService.deleteRoutineListByUserAndRoutine(userId, routineId);
        return "redirect:/routine/list";
    }

    @GetMapping("/detail/{routineId}")
    public String routineDetail(@PathVariable int routineId, Model model) {
        RoutineDetailDto routine = routineService.getRoutineDetail(routineId);
        model.addAttribute("routine", routine);
        return "routine/RoutineDetail";
    }


}
