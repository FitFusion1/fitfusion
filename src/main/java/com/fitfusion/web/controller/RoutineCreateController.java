package com.fitfusion.web.controller;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

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
        RoutineDetailDto routine = new RoutineDetailDto();
        List<Exercise> exerciseList = exerciseService.getAllExercises();
        routine.setExercises(new ArrayList<>());
        model.addAttribute("routine", routine);
        model.addAttribute("exerciseList", exerciseList);
        return "routine/RoutineEdit";
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

    @PostMapping("/save/custom")
    public String saveCustomRoutine(@ModelAttribute RoutineDetailDto routine) {
        routineService.saveCustomRoutine(userId, routine);
        return "redirect:/routine/list";
    }
}
