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

    @GetMapping("/routinelist")
    public String RoutineListPage(Model model) {
        return "myexercise/RoutineList";
    }

    @GetMapping("/routinedetail")
    public String RoutineDetailPage(Model model) {
        return "myexercise/RoutineDetail";
    }

    @GetMapping("/exerciselog")
    public String ExerciseLogPage(Model model) {
        return "myexercise/ExerciseLog";
    }

    @GetMapping("/routinecompletion")
    public String RoutineCompletionPage(Model model) {
        return "myexercise/Routinecompletion";
    }

    @GetMapping("/exercisestatus")
    public String ExerciseStatusPage(Model model) {
        return "myexercise/ExerciseStatus";
    }

    @GetMapping("/targetroutine")
    public String ExerciseRecommendationPage(Model model) {
        return "myexercise/TargetRoutineRecommendations";
    }

    @GetMapping("/routine")
    public String RecommendationRoutinePage(Model model) {
        return "myexercise/RoutineRecommendation";
    }

    @GetMapping("/condition")
    public String ExerciseConditionPage(Model model) {
        return "myexercise/ExerciseCondition";
    }

    @GetMapping("/goalupdate")
    public String GoalUpdatePage(Model model) {
        return "myexercise/GoalUpdate";
    }

    @GetMapping("/routineedit")
    public String RoutineEditPage(Model model) {
        return "myexercise/RoutineEdit";
    }

    @GetMapping("/exerciselogedit")
    public String ExerciseLogEditPage(Model model) {
        return "myexercise/ExerciseLogEdit";
    }


}
