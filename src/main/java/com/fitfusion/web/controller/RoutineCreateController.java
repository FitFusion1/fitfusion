package com.fitfusion.web.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitfusion.dto.RoutineDetailDto;
import com.fitfusion.dto.TargetRoutineDto;
import com.fitfusion.enums.BodyPart;
import com.fitfusion.security.SecurityUser;
import com.fitfusion.service.*;
import com.fitfusion.vo.Exercise;
import com.fitfusion.vo.ExerciseGoal;
import com.fitfusion.vo.RecommendedExercise;
import com.fitfusion.web.form.ExerciseConditionForm;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/routine")
public class RoutineCreateController {

    private final ExerciseConditionService conditionService;
    private final ExerciseService exerciseService;
    private final AiRoutineGenerator aiRoutineGenerator;
    private final RoutineService routineService;
    private final TargetPartRoutineGenerator targetPartRoutineGenerator;
    private final TargetPartRoutineService targetPartRoutineService;
    private final ExerciseGoalService exerciseGoalService;
    private final ObjectMapper objectMapper;

    @GetMapping("/create/ai")
    public String aiRoutine(@AuthenticationPrincipal SecurityUser user, HttpSession session, RedirectAttributes redirectAttributes, Model model) {
        try {

            ExerciseGoal goal = exerciseGoalService.getSelectedGoalEntityByUserId(user.getUser().getUserId());
            String goalType = (goal != null) ? goal.getGoalType() : null;
            Boolean conditionSet = (Boolean) session.getAttribute("conditionSet");

            if ("근육 증가".equals(goalType) && !Boolean.TRUE.equals(conditionSet)) {
                redirectAttributes.addFlashAttribute("alertMessage", "근육 증가 목표는 사용자 맞춤 설정이 필요합니다. 루틴을 직접 선택해주세요.");
                return "redirect:/condition/save";
            }

            ExerciseConditionForm condition = conditionService.getConditionAndAvoidAndTargetByUserId(user.getUser().getUserId());

            if (condition == null) {
                @SuppressWarnings("unchecked")
                List<String> avoid = (List<String>) session.getAttribute("avoidParts");
                @SuppressWarnings("unchecked")
                List<String> target = (List<String>) session.getAttribute("targetParts");
                String level = (String) session.getAttribute("condition");

                ExerciseConditionForm tmp = new ExerciseConditionForm();
                tmp.setAvoidParts(avoid != null ? avoid : Collections.emptyList());
                tmp.setTargetParts(target != null ? target : Collections.emptyList());
                tmp.setConditionLevel(level);
                condition = tmp;
            }

            List<Exercise> exercises = exerciseService.getAllExercises();
            List<RecommendedExercise> recommended = aiRoutineGenerator.generateRoutine(condition, exercises, goalType);

            for (RecommendedExercise re : recommended) {
                Exercise fullInfo = exerciseService.getExerciseById(re.getExerciseId());
                re.setExercise(fullInfo);
            }

            model.addAttribute("recommendedExercises", recommended);
            model.addAttribute("routineName", "오늘의 AI 추천 루틴");
            session.setAttribute("recommendedExercises", recommended);

            return "routine/RoutineRecommendation";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "추천 루틴 생성 중 오류가 발생했습니다.");
            return "routine/RoutineRecommendation";
        }
    }

    @PostMapping("/save/ai")
    public String saveRecommendedRoutine(@AuthenticationPrincipal SecurityUser user,
                                         HttpSession session,
                                         RedirectAttributes redirect)  {

        @SuppressWarnings("unchecked")
        List<RecommendedExercise> recommended = (List<RecommendedExercise>) session.getAttribute("recommendedExercises");

        if (recommended == null || recommended.isEmpty()) {
            redirect.addFlashAttribute("alertMessage", "저장할 추천 루틴이 없습니다. 다시 생성해 주세요");
            return "redirect:/routine/create/ai";
        }

        routineService.saveRecommendedRoutine(user.getUser().getUserId(), recommended);
        session.removeAttribute("recommendedExercises");
        return "redirect:/routine/list";
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
    public String saveCustomRoutine(@AuthenticationPrincipal SecurityUser user, @ModelAttribute RoutineDetailDto routine, RedirectAttributes redirectAttributes) {

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

        for (int i = 0; i < routine.getExercises().size(); i++) {
            System.out.println("DEBUG => exercise index=" + i
                    + ", sets=" + routine.getExercises().get(i).getSets()
                    + ", reps=" + routine.getExercises().get(i).getReps());
        }


        routineService.saveCustomRoutine(user.getUser().getUserId(), routine);
        return "redirect:/routine/list";
    }

    @PostMapping("/update/custom")
    public String updateCustomRoutine(@AuthenticationPrincipal SecurityUser user, @ModelAttribute RoutineDetailDto routine) {
        routineService.updateCustomRoutine(user.getUser().getUserId(), routine);
        return "redirect:/routine/list";
    }

    @GetMapping("/create/target")
    public String targetRecommend(@AuthenticationPrincipal SecurityUser user, Model model) throws Exception{

        List<BodyPart> lackParts = targetPartRoutineService.findLackParts(user.getUser().getUserId());
        List<Exercise> exercises = exerciseService.getAllExercises();

        Map<BodyPart, List<RecommendedExercise>> routines =
                targetPartRoutineGenerator.generate(lackParts, exercises);

        model.addAttribute("routines", routines);
        return "routine/TargetRoutineRecommendations";
    }

    @PostMapping("/save/target")
    public String saveTargetRoutine(@AuthenticationPrincipal SecurityUser user, TargetRoutineDto targetRoutineDto) throws Exception{

        List<RecommendedExercise> exercises = objectMapper.readValue(
                targetRoutineDto.getRoutineJson(),
                new TypeReference<List<RecommendedExercise>>() {});

        routineService.saveTargetRoutine(user.getUser().getUserId(),targetRoutineDto.getBodyPart(), exercises);

        return "redirect:/routine/list";
    }

}
