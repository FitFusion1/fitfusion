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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
                        BindingResult bindingResult,
                        Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("goalTypes", GoalType.values());
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
                        BindingResult bindingResult,
                        Model model) {


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
                        BindingResult bindingResult,
                        Model model) {
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
                        SessionStatus status,
                        Model model) {
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

        if (selectedGoal != null) {
            exerciseGoals.sort((g1, g2) -> {
                if (g1.getGoalId() == selectedGoal.getGoalId()) return -1;
                if (g2.getGoalId() == selectedGoal.getGoalId()) return 1;
                return 0;
            });
        }

        model.addAttribute("exerciseGoals", exerciseGoals);
        model.addAttribute("selectedGoal", selectedGoal);

        return "exerciseGoal/ExerciseGoalList";
    }

    @PostMapping("/select")
    public String selectGoal(@RequestParam int goalId, RedirectAttributes redirectAttributes) {
        SelectedGoal currentSelected = selectedGoalService.getSelectedGoal(userId);

        if (currentSelected != null && currentSelected.getGoalId() == goalId) {
            return "redirect:/exercisegoal/goallist";
        }

        selectedGoalService.selectGoal(userId, goalId);

        ExerciseGoal goal = exerciseGoalService.getUserGoalByUserId(userId, goalId);
        String goalType = goal.getGoalType();

        String message = switch (goalType) {
            case "체중 감량" -> "유산소 및 하체 중심의 고강도 루틴이 추천됩니다.";
            case "체중 증가" -> "가슴, 등, 다리 중심의 고강도 루틴이 추천됩니다.";
            case "체중 유지" -> "전신을 고르게 자극하는 중간 강도 루틴이 제공됩니다.";
            case "근육 증가" -> "사용자가 선택한 부위 중심의 고강도 루틴이 추천됩니다.";
            case "건강한 생활습관 개선" -> "코어, 유산소, 하체 중심의 가벼운 루틴이 추천됩니다.";
            default -> null;
        };

        if (message != null) {
            redirectAttributes.addFlashAttribute("goalAlert", message);
        }

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
