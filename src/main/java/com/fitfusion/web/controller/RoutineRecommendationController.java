package com.fitfusion.web.controller;

import com.fitfusion.service.*;
import com.fitfusion.vo.Exercise;
import com.fitfusion.vo.RecommendedExercise;
import com.fitfusion.vo.RoutineExercise;
import com.fitfusion.web.form.ExerciseConditionForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class RoutineRecommendationController {

    private final ExerciseConditionService conditionService;
    private final ExerciseService exerciseService;
    private final AiRoutineGenerator aiRoutineGenerator;
    private final RoutineService routineService;

    private final int userId = 1;
    private final ExerciseGoalService exerciseGoalService;

    @GetMapping("/routine/create")
    public String recommendRoutine(Model model) {
        try {
            ExerciseConditionForm condition = conditionService.getConditionAndAvoidAndTargetByUserId(userId);
            List<Exercise> exercises = exerciseService.getAllExercises();
            String goalType = exerciseGoalService.getSelectedGoalByUserId(userId).getGoalType();
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

    @PostMapping("/routine/save")
    public String saveRecommendedRoutine() throws Exception {
        ExerciseConditionForm condition = conditionService.getConditionAndAvoidAndTargetByUserId(userId);
        List<Exercise> allExercises = exerciseService.getAllExercises();
        String goalType = exerciseGoalService.getSelectedGoalByUserId(userId).getGoalType();


        List<RecommendedExercise> recommendedExercises = aiRoutineGenerator.generateRoutine(condition, allExercises, goalType);
        routineService.saveRecommendedRoutine(userId, recommendedExercises);
        return "redirect:/routine/list";
    }

}
