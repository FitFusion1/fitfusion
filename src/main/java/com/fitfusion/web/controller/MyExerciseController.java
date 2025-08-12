package com.fitfusion.web.controller;

import com.fitfusion.dto.ExerciseLogDto;
import com.fitfusion.security.SecurityUser;
import com.fitfusion.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/myexercise")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class MyExerciseController {

    private final ExerciseStatusService exerciseStatusService;

    @GetMapping
    public String MyExercisePage() {
        return "myexercise/MyExerciseMain";
    }


    @GetMapping("/exercisestatus")
    public String ExerciseStatusPage(Model model, @AuthenticationPrincipal SecurityUser user) {

        Map<String, Object> status = exerciseStatusService.getExerciseStatus(user.getUser().getUserId());
        model.addAttribute("exerciseStatus", status);

        return "myexercise/ExerciseStatus";
    }


}
