package com.fitfusion.web.rest;

import com.fitfusion.dto.RecommendationResponseDto;
import com.fitfusion.dto.SaveRecommendRoutineRequestDto;
import com.fitfusion.security.SecurityUser;
import com.fitfusion.service.*;
import com.fitfusion.vo.Exercise;
import com.fitfusion.vo.ExerciseGoal;
import com.fitfusion.vo.RecommendedExercise;
import com.fitfusion.web.form.ExerciseConditionForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/routine")
@RequiredArgsConstructor
public class RoutineCreateRestController {

    private final ExerciseService exerciseService;
    private final ExerciseConditionService conditionService;
    private final ExerciseGoalService exerciseGoalService;
    private final AiRoutineGenerator aiRoutineGenerator;
    private final RoutineService routineService;

    @GetMapping("/create/ai")
    public ResponseEntity<?> aiRoutine(@AuthenticationPrincipal SecurityUser user,
                                       @RequestParam(name = "conditionSet", defaultValue = "false") boolean conditionSet) throws Exception {
        ExerciseConditionForm condition = conditionService.getConditionAndAvoidAndTargetByUserId(user.getUser().getUserId());
        List<Exercise> exercises = exerciseService.getAllExercises();
        ExerciseGoal goal = exerciseGoalService.getSelectedGoalEntityByUserId(user.getUser().getUserId());
        String goalType = (goal != null) ? goal.getGoalType() : null;

        if ("근육 증가".equals(goalType) && (condition == null || !conditionSet)) {
            return ResponseEntity.status(409).body(Map.of(
               "code", "NEED_CONDITION_SETUP",
               "message", "근육 증가 목표는 사용자 맞춤 설정이 필요합니다. 루틴을 직접 선택하거나 조건을 설정하세요",
               "next", "/condition/save"
            ));
        }

        List<RecommendedExercise> recommended = aiRoutineGenerator.generateRoutine(condition, exercises, goalType);

        for (RecommendedExercise re : recommended) {
            re.setExercise(exerciseService.getExerciseById(re.getExerciseId()));
        }

        RecommendationResponseDto body = new RecommendationResponseDto("오늘의 AI 추천 루틴", recommended);
        return ResponseEntity.ok(body);
    }

    @PostMapping("/recommendations/ai/save")
    public ResponseEntity<?> save(@AuthenticationPrincipal SecurityUser user,
                                  @Valid @RequestBody SaveRecommendRoutineRequestDto request) {
        int routineId = routineService.saveRecommendedRoutine(user.getUser().getUserId(), request.getExercises());

        return ResponseEntity.created(URI.create("/api/routine/" + routineId))
                .body(Map.of("routineId", routineId, "message", "저장 완료"));
    }
}
