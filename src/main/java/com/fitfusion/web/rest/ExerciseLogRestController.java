package com.fitfusion.web.rest;

import com.fitfusion.dto.ExerciseLogResponseDto;
import com.fitfusion.dto.RoutineLogDto;
import com.fitfusion.security.SecurityUser;
import com.fitfusion.service.ExerciseLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exerciseLog")
@RequiredArgsConstructor
public class ExerciseLogRestController {

    private final ExerciseLogService exerciseLogService;

    @GetMapping
    public ResponseEntity<ExerciseLogResponseDto> getExerciseLog(@AuthenticationPrincipal SecurityUser user,
                                                                 @RequestParam(defaultValue = "1") int page,
                                                                 @RequestParam(defaultValue = "1") int size) {
        List<RoutineLogDto> logs = exerciseLogService.getExerciseLogsByUserId(user.getUser().getUserId(), page, size);
        long totalRoutines = exerciseLogService.countLogDistinctSessionByUserId(user.getUser().getUserId());
        int totalPage = (int)Math.ceil((double) totalRoutines / size);

        ExerciseLogResponseDto.Meta meta = new ExerciseLogResponseDto.Meta(
                page,
                size,
                totalRoutines,
                totalPage
        );

        return ResponseEntity.ok(new ExerciseLogResponseDto(logs, meta));
    }
    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> deleteExecute(@AuthenticationPrincipal SecurityUser user, @PathVariable("sessionId") int sessionId){

        exerciseLogService.deleteExerciseLog(user.getUser().getUserId(), sessionId);
        return ResponseEntity.noContent().build();
    }
}
