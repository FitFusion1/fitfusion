    package com.fitfusion.web.controller;

import com.fitfusion.dto.RoutineListDto;
import com.fitfusion.dto.RoutineListResponseDto;
import com.fitfusion.security.SecurityUser;
import com.fitfusion.service.RoutineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/routines")
public class RoutineListRestController {

    private final RoutineService routineService;

    @GetMapping
    public ResponseEntity<RoutineListResponseDto> list(@AuthenticationPrincipal SecurityUser user,
                                                     @RequestParam(defaultValue = "1") int page,
                                                     @RequestParam(defaultValue = "6") int size) {

        List<RoutineListDto> routines = routineService.getRoutineListByUserId(user.getUser().getUserId(), page, size);
        long total = routineService.countRoutineByUserId(user.getUser().getUserId());

        RoutineListResponseDto.Meta meta = new RoutineListResponseDto.Meta(
                page,
                size,
                total,
                (int) Math.ceil((double) total / size)
        );

        return ResponseEntity.ok(new RoutineListResponseDto(routines, meta));
    }

    @DeleteMapping("/{routineId}")
    public ResponseEntity<Void> deleteRoutine(@AuthenticationPrincipal SecurityUser user, @PathVariable int routineId) {
        routineService.deleteRoutineListByUserAndRoutine(user.getUser().getUserId(), routineId);
        return ResponseEntity.noContent().build();
    }
}
