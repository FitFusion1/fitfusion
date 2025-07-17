package com.fitfusion.web.controller;

import com.fitfusion.service.ExerciseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/myexercise")
public class MyExerciseController {

    @Autowired
    private ExerciseService exerciseService;

    @GetMapping("")
    public String MyExercisePage(Model model) {
        return "myexercise/MyExerciseMain";
    }

    @GetMapping("/exerciselog")
    public String ExerciseLogPage(Model model) {
        return "myexercise/ExerciseLog";
    }

    @GetMapping("/exercisestatus")
    public String ExerciseStatusPage(Model model) {
        return "myexercise/ExerciseStatus";
    }

    @GetMapping("/exerciselogedit")
    public String ExerciseLogEditPage(Model model) {
        return "myexercise/ExerciseLogEdit";
    }


}
