package com.fitfusion.web.controller;

import com.fitfusion.enums.GoalType;
import com.fitfusion.mapper.ExerciseGoalMapper;
import com.fitfusion.service.ExerciseGoalService;
import com.fitfusion.service.SelectedGoalService;
import com.fitfusion.validation.Step1Group;
import com.fitfusion.validation.Step2Group;
import com.fitfusion.validation.Step3Group;
import com.fitfusion.validation.Step4Group;
import com.fitfusion.vo.Exercise;
import com.fitfusion.vo.ExerciseGoal;
import com.fitfusion.vo.SelectedGoal;
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
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/exercisegoal")
@SessionAttributes("exerciseGoalForm")
public class ExerciseGoalController {

    private final ExerciseGoalService exerciseGoalService;
    private final SelectedGoalService selectedGoalService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ExerciseGoalMapper exerciseGoalMapper;
    int userId = 1;

    @GetMapping("/step1")
    public String step1(Model model) {
        model.addAttribute("exerciseGoalForm", new ExerciseGoalRegisterForm());
        model.addAttribute("goalTypes", GoalType.values());
        return "exerciseGoal/CreateGoalOne";
    }

    @PostMapping("/step1")
    public String step1(@Validated(Step1Group.class) @ModelAttribute("exerciseGoalForm") ExerciseGoalRegisterForm formData,
                        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "exerciseGoal/CreateGoalOne";
        }

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

        formData.setUserId(userId);
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

        List<ExerciseGoal> exerciseGoals = exerciseGoalMapper.getAllUserGoalsByUserId(userId);
        SelectedGoal selectedGoal = selectedGoalService.getSelectedGoal(userId);

        model.addAttribute("exerciseGoals", exerciseGoals);
        model.addAttribute("selectedGoal", selectedGoal);

        return "exerciseGoal/ExerciseGoalList";
    }

    @PostMapping("/select")
    public String selectGoal(@RequestParam int goalId) {
        selectedGoalService.deleteSelectedGoal(userId);
        selectedGoalService.selectGoal(userId, goalId);

        return "redirect:/exercisegoal/goallist";
    }

    @PostMapping("/unselect")
    public String unselectGoal() {
        selectedGoalService.deleteSelectedGoal(userId);

        return "redirect:/exercisegoal/goallist";
    }

    @GetMapping("/goalupdate")
    public String goalUpdate(@RequestParam("goalId") int goalId, Model model) {
        ExerciseGoal exerciseGoal = exerciseGoalService.getUserGoalByUserId(userId, goalId);
        ExerciseGoalRegisterForm formData = modelMapper.map(exerciseGoal, ExerciseGoalRegisterForm.class);
        model.addAttribute("exerciseGoalForm", formData);
        model.addAttribute("goalTypes", GoalType.values());
        return "exerciseGoal/GoalUpdate";
    }

    @PostMapping("/goalupdate")
    public String goalUpdate(@Valid @ModelAttribute("exerciseGoalForm") ExerciseGoalRegisterForm formData,
                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "exerciseGoal/GoalUpdate";
        }
        formData.setUserId(userId);
        exerciseGoalService.updateGoal(formData);
        return "redirect:/exercisegoal/goallist";
    }

    @PostMapping("/goaldelete")
    public String goalDelete(@RequestParam("goalId") int goalId) {
        exerciseGoalService.deleteGoal(goalId);
        return "redirect:/exercisegoal/goallist";
    }

}
