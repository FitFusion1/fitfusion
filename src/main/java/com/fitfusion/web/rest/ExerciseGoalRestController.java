package com.fitfusion.web.rest;

import com.fitfusion.dto.ExerciseGoalDto;
import com.fitfusion.dto.GoalsResponseDto;
import com.fitfusion.dto.SelectGoalResponseDto;
import com.fitfusion.dto.SelectedGoalDto;
import com.fitfusion.security.SecurityUser;
import com.fitfusion.service.ExerciseConditionService;
import com.fitfusion.service.ExerciseGoalService;
import com.fitfusion.service.SelectedGoalService;
import com.fitfusion.vo.SelectedGoal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class ExerciseGoalRestController {

    private final ExerciseGoalService exerciseGoalService;
    private final SelectedGoalService selectedGoalService;
    private final ExerciseConditionService exerciseConditionService;

    @GetMapping
    public ResponseEntity<GoalsResponseDto> list(@AuthenticationPrincipal SecurityUser user) {
        List<ExerciseGoalDto> goals = exerciseGoalService.getAllGoalByUserId(user.getUser().getUserId());

        SelectedGoal selected = selectedGoalService.getSelectedGoal(user.getUser().getUserId());
        SelectedGoalDto selectedGoal = (selected != null) ? new SelectedGoalDto(selected) : null;

        GoalsResponseDto response = new GoalsResponseDto(goals, selectedGoal);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{goalId}/select")
    public ResponseEntity<SelectGoalResponseDto> selectGoal(@PathVariable int goalId, @AuthenticationPrincipal SecurityUser user) {
        SelectGoalResponseDto response = selectedGoalService.selectGoalForResponse(user.getUser().getUserId(), goalId);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/selected")
    public ResponseEntity<Void> deleteSelectGoal(@AuthenticationPrincipal SecurityUser user) {
        selectedGoalService.deleteSelectedGoal(user.getUser().getUserId());
        exerciseConditionService.deleteConditionByUserId(user.getUser().getUserId());

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{goalId}")
    public ResponseEntity<Void> deleteGoal(@AuthenticationPrincipal SecurityUser user, @PathVariable int goalId) {

        SelectedGoal selectedGoal = selectedGoalService.getSelectedGoal(user.getUser().getUserId());

        if (selectedGoal != null&& selectedGoal.getGoalId() == goalId) {
            selectedGoalService.deleteSelectedGoal(user.getUser().getUserId());
        }

        exerciseGoalService.deleteGoal(goalId, user.getUser().getUserId());

        return ResponseEntity.noContent().build();
    }


}
