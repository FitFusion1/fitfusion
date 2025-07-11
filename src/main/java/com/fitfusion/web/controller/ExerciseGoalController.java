package com.fitfusion.web.controller;

import com.fitfusion.service.ExerciseGoalService;
import com.fitfusion.validation.Step1Group;
import com.fitfusion.validation.Step2Group;
import com.fitfusion.validation.Step3Group;
import com.fitfusion.validation.Step4Group;
import com.fitfusion.vo.Exercise;
import com.fitfusion.vo.ExerciseGoal;
import com.fitfusion.web.form.ExerciseGoalRegisterForm;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.util.Date;

@Controller
@RequiredArgsConstructor
@RequestMapping("/exercisegoal")
@SessionAttributes("exerciseGoalForm")
public class ExerciseGoalController {

    private final ExerciseGoalService exerciseGoalService;
    @Autowired
    private ModelMapper modelMapper;


    @GetMapping("/step1")
    public String step1(Model model) {
        model.addAttribute("exerciseGoalForm", new ExerciseGoalRegisterForm());
        return "exerciseGoal/CreateGoalOne";
    }

    @PostMapping("/step1")
    public String step1(@Validated(Step1Group.class) @ModelAttribute("exerciseGoalForm") ExerciseGoalRegisterForm formData,
                        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "exerciseGoal/CreateGoalOne";
        }
        System.out.println(">> STEP1 저장한 값: " + formData.getStartWeight());
        return "redirect:/exercisegoal/step2";
    }

    @GetMapping("/step2")
    public String step2(@ModelAttribute("exerciseGoalForm") ExerciseGoalRegisterForm formData,
                        Model model) {
        model.addAttribute("exerciseGoalForm", formData);
        return "exerciseGoal/CreateGoalTwo";
    }

    @PostMapping("/step2")
    public String step2(@Validated(Step2Group.class) @ModelAttribute("exerciseGoalForm") ExerciseGoalRegisterForm formData,
                        BindingResult bindingResult) {
        System.out.println(">> STEP2 세션에서 꺼낸 값: " + formData);

        if (bindingResult.hasErrors()) {
            return "exerciseGoal/CreateGoalTwo";
        }
        return "redirect:/exercisegoal/step3";
    }

    @GetMapping("/step3")
    public String step3(@ModelAttribute("exerciseGoalForm") ExerciseGoalRegisterForm formData) {
        return "exerciseGoal/CreateGoalThree";
    }

    @PostMapping("/step3")
    public String step3(@Validated(Step3Group.class) @ModelAttribute("exerciseGoalForm") ExerciseGoalRegisterForm formData,
                        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "exerciseGoal/CreateGoalThree";
        }
        return "redirect:/exercisegoal/step4";
    }

    @GetMapping("/step4")
    public String step4(@ModelAttribute("exerciseGoalForm") ExerciseGoalRegisterForm formData) {
        return "exerciseGoal/CreateGoalFour";
    }

    @PostMapping("/step4")
    public String step4(@Validated(Step4Group.class) @ModelAttribute("exerciseGoalForm") ExerciseGoalRegisterForm formData,
                        BindingResult bindingResult,
                        SessionStatus status) {
        if (bindingResult.hasErrors()) {
            return "exerciseGoal/CreateGoalFour";
        }

        formData.setUserId(1);
        exerciseGoalService.insertUserGoal(formData);

        status.setComplete();
        return "redirect:/exercisegoal/success";
    }

    @GetMapping("/success")
    public String success(Model model) {
        return "exerciseGoal/CreateGoalSuccess";
    }

    @GetMapping("/goallist")
    public String goalList(Model model) {
        return "exerciseGoal/ExerciseGoalList";
    }



}
