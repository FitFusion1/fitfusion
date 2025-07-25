package com.fitfusion.web.controller;

import com.fitfusion.dto.ExerciseItemDto;
import com.fitfusion.dto.RoutineDetailDto;
import com.fitfusion.dto.RoutineListDto;
import com.fitfusion.service.*;
import com.fitfusion.vo.Exercise;
import com.fitfusion.vo.ExerciseGoal;
import com.fitfusion.vo.RecommendedExercise;
import com.fitfusion.vo.Routine;
import com.fitfusion.web.form.ExerciseConditionForm;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/routine")
public class RoutineCreateController {

    private final ExerciseConditionService conditionService;
    private final ExerciseService exerciseService;
    private final AiRoutineGenerator aiRoutineGenerator;
    private final RoutineService routineService;

    private final int userId = 1;
    private final ExerciseGoalService exerciseGoalService;

    @GetMapping("/create/ai")
    public String aiRoutine(HttpSession session, RedirectAttributes redirectAttributes, Model model) {
        try {
            ExerciseConditionForm condition = conditionService.getConditionAndAvoidAndTargetByUserId(userId);
            List<Exercise> exercises = exerciseService.getAllExercises();

            ExerciseGoal goal = exerciseGoalService.getSelectedGoalByUserId(userId);
            String goalType = (goal != null) ? goal.getGoalType() : null;
            Boolean conditionSet = (Boolean) session.getAttribute("conditionSet");

            if ("근육 증가".equals(goalType) && (condition == null || conditionSet == null || !conditionSet)) {
                redirectAttributes.addFlashAttribute("alertMessage", "근육 증가 목표는 사용자 맞춤 설정이 필요합니다. 루틴을 직접 선택해주세요.");
                return "redirect:/condition/save";
            }


            List<RecommendedExercise> recommended = aiRoutineGenerator.generateRoutine(condition, exercises, goalType);

            for (RecommendedExercise re : recommended) {
                Exercise fullInfo = exerciseService.getExerciseById(re.getExerciseId());
                re.setExercise(fullInfo);
            }

            model.addAttribute("recommendedExercises", recommended);
            model.addAttribute("routineName", "오늘의 AI 추천 루틴");


            return "routine/RoutineRecommendation";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "추천 루틴 생성 중 오류가 발생했습니다.");
            return "routine/RoutineRecommendation";
        }
    }

    @GetMapping("/create/custom")
    public String customRoutine(Model model) {
        if (!model.containsAttribute("routine")) {
            RoutineDetailDto routine = new RoutineDetailDto();
            routine.setExercises(new ArrayList<>());
            model.addAttribute("routine", routine);
        }

        model.addAttribute("exerciseList", exerciseService.getAllExercises());
        return "routine/RoutineEdit";
    }

    @PostMapping("/save/custom")
    public String saveCustomRoutine(@ModelAttribute RoutineDetailDto routine, RedirectAttributes redirectAttributes) {

        boolean hasError = false;
        Map<Integer, String> setsErrors = new HashMap<>();
        Map<Integer, String> repsErrors = new HashMap<>();

        if (routine.getRoutineName() == null || routine.getRoutineName().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("routineError", "루틴 이름을 입력해주세요.");
            hasError = true;
        }


        for (int i = 0; i < routine.getExercises().size(); i++) {
            Integer sets = routine.getExercises().get(i).getSets();
            Integer reps = routine.getExercises().get(i).getReps();

            if (sets == null || sets == 0) {
                setsErrors.put(i, "세트 수는 0보다 커야합니다.");
                hasError = true;
            }
            if (reps == null || reps == 0) {
                repsErrors.put(i, "반복 수는 0보다 커야합니다.");
                hasError = true;
            }
        }

        if (hasError){
            redirectAttributes.addFlashAttribute("routine", routine);
            redirectAttributes.addFlashAttribute("setsErrors", setsErrors);
            redirectAttributes.addFlashAttribute("repsErrors", repsErrors);
            return "redirect:/routine/create/custom";
        }

        routineService.saveCustomRoutine(userId, routine);
        return "redirect:/routine/list";
    }

    @PostMapping("/save/ai")
    public String saveRecommendedRoutine() throws Exception {

        ExerciseConditionForm condition = conditionService.getConditionAndAvoidAndTargetByUserId(userId);
        List<Exercise> allExercises = exerciseService.getAllExercises();
        String goalType = exerciseGoalService.getSelectedGoalByUserId(userId).getGoalType();


        List<RecommendedExercise> recommendedExercises = aiRoutineGenerator.generateRoutine(condition, allExercises, goalType);
        routineService.saveRecommendedRoutine(userId, recommendedExercises);
        return "redirect:/routine/list";
    }

    @PostMapping("/update/custom")
    public String updateCustomRoutine(@ModelAttribute RoutineDetailDto routine) {
        routineService.updateCustomRoutine(userId, routine);
        return "redirect:/routine/list";
    }

}
