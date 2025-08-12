package com.fitfusion.web.rest;

import com.fitfusion.dto.ExerciseGoalDto;
import com.fitfusion.dto.ExerciseLogDto;
import com.fitfusion.dto.ExerciseStatusDto;
import com.fitfusion.dto.RoutineDto;
import com.fitfusion.security.SecurityUser;
import com.fitfusion.service.ExerciseGoalService;
import com.fitfusion.service.ExerciseLogService;
import com.fitfusion.service.ExerciseStatusService;
import com.fitfusion.service.RoutineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/myExercise")
public class MyExerciseRestController {

    private final ExerciseStatusService exerciseStatusService;
    private final RoutineService routineService;
    private final ExerciseLogService exerciseLogService;
    private final ExerciseGoalService goalService;

    @GetMapping("/goal")
    public ResponseEntity<ExerciseGoalDto> getGoal(@AuthenticationPrincipal SecurityUser user) {
        return ResponseEntity.ok(goalService.getSelectedGoalDtoByUserId(user.getUser().getUserId()));
    }

    @GetMapping("/exercise-status")
    public ResponseEntity<ExerciseStatusDto> getExerciseStatus(@AuthenticationPrincipal SecurityUser user) {
        return ResponseEntity.ok(exerciseStatusService.getExerciseStatusDto(user.getUser().getUserId()));
    }

    @GetMapping("/routines")
    public ResponseEntity<List<RoutineDto>> getRoutines(@AuthenticationPrincipal SecurityUser user) {
        return ResponseEntity.ok(routineService.getRecentRoutine(user.getUser().getUserId()));
    }

    @GetMapping("/exercise-logs")
    public ResponseEntity<List<ExerciseLogDto>> getExerciseLogs(@AuthenticationPrincipal SecurityUser user) {
        return ResponseEntity.ok(exerciseLogService.getFourExerciseLogsByUserId(user.getUser().getUserId()));
    }
}
