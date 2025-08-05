package com.fitfusion.web.controller;

import com.fitfusion.enums.GoalType;
import com.fitfusion.mapper.AvoidPartsMapper;
import com.fitfusion.mapper.ExerciseGoalMapper;
import com.fitfusion.mapper.TargetPartsMapper;
import com.fitfusion.security.SecurityUser;
import com.fitfusion.service.ExerciseConditionService;
import com.fitfusion.service.ExerciseGoalService;
import com.fitfusion.service.SelectedGoalService;
import com.fitfusion.validation.Step1Group;
import com.fitfusion.validation.Step2Group;
import com.fitfusion.validation.Step3Group;
import com.fitfusion.validation.Step4Group;
import com.fitfusion.vo.ExerciseGoal;
import com.fitfusion.vo.SelectedGoal;
import com.fitfusion.web.form.ExerciseGoalRegisterForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    @Autowired
    private ExerciseConditionService exerciseConditionService;
    @Autowired
    private TargetPartsMapper targetPartsMapper;
    @Autowired
    private AvoidPartsMapper avoidPartsMapper;

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
    public String step4(@AuthenticationPrincipal SecurityUser user, @Validated(Step4Group.class) @ModelAttribute("exerciseGoalForm") ExerciseGoalRegisterForm formData,
                        BindingResult bindingResult,
                        SessionStatus status) {
        if (bindingResult.hasErrors()) {
            return "exerciseGoal/CreateGoalFour";
        }

        formData.setUserId(user.getUser().getUserId());
        exerciseGoalService.insertUserGoalWithSelection(formData);

        status.setComplete();
        return "redirect:/exercisegoal/success";
    }

    @GetMapping("/success")
    public String success() {
        return "exerciseGoal/CreateGoalSuccess";
    }

    @GetMapping("/goallist")
    public String goalList() {
        return "exerciseGoal/ExerciseGoalList";
    }

    @PostMapping("/select")
    public String selectGoal(@AuthenticationPrincipal SecurityUser user, @RequestParam int goalId, RedirectAttributes redirectAttributes) {
        SelectedGoal currentSelected = selectedGoalService.getSelectedGoal(user.getUser().getUserId());

        if (currentSelected != null && currentSelected.getGoalId() == goalId) {
            return "redirect:/exercisegoal/goallist";
        }

        selectedGoalService.selectGoal(user.getUser().getUserId(), goalId);

        ExerciseGoal goal = exerciseGoalService.getUserGoalByUserId(user.getUser().getUserId(), goalId);
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
    public String unselectGoal(@AuthenticationPrincipal SecurityUser user) {
        selectedGoalService.deleteSelectedGoal(user.getUser().getUserId());

        return "redirect:/exercisegoal/goallist";
    }

    @GetMapping("/goalupdate/{goalId}")
    public String goalUpdate(@AuthenticationPrincipal SecurityUser user, @PathVariable("goalId") int goalId, Model model) {
        ExerciseGoal exerciseGoal = exerciseGoalService.getUserGoalByUserId(user.getUser().getUserId(), goalId);
        ExerciseGoalRegisterForm formData = modelMapper.map(exerciseGoal, ExerciseGoalRegisterForm.class);
        model.addAttribute("exerciseGoalForm", formData);
        model.addAttribute("goalTypes", GoalType.values());
        return "exerciseGoal/GoalUpdate";
    }

    @PostMapping("/goalupdate")
    public String goalUpdate(@AuthenticationPrincipal SecurityUser user, @Valid @ModelAttribute("exerciseGoalForm") ExerciseGoalRegisterForm formData,
                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "exerciseGoal/GoalUpdate";
        }
        formData.setUserId(user.getUser().getUserId());
        exerciseGoalService.updateGoal(formData);
        return "redirect:/exercisegoal/goallist";
    }

    @DeleteMapping("/goaldelete/{goalId}")
    public String goalDelete(@AuthenticationPrincipal SecurityUser user, @PathVariable int goalId) {
        SelectedGoal selectedGoal = selectedGoalService.getSelectedGoal(user.getUser().getUserId());

        if (selectedGoal != null && selectedGoal.getGoalId() == goalId) {
            selectedGoalService.deleteSelectedGoal(user.getUser().getUserId());
        }

        exerciseGoalService.deleteGoal(goalId);
        return "redirect:/exercisegoal/goallist";
    }

    @PostMapping("/checkgoal")
    public String checkGoalAndCreateRoutine(@AuthenticationPrincipal SecurityUser user){
        ExerciseGoal goal = exerciseGoalService.getSelectedGoalEntityByUserId(user.getUser().getUserId());
        String goalType = (goal != null) ? goal.getGoalType() : null;

        // 근육 증가일 경우만 /condition/save로 이동
        if ("근육 증가".equals(goalType)) {
            return "redirect:/condition/save";
        }

        // 그 외(목표가 없거나 근육 증가가 아닌 경우)는 기존 컨디션 삭제
        int conditionId = exerciseConditionService.getConditionIdByUserId(user.getUser().getUserId());
        if (conditionId > 0) {
            exerciseConditionService.deleteTargetPartsByConditionId(conditionId);
            exerciseConditionService.deleteAvoidPartsByConditionId(conditionId);
            exerciseConditionService.deleteConditionByUserId(user.getUser().getUserId());
        }

        return "redirect:/routine/create/ai";
    }

    @GetMapping("/goaldetail/{goalId}")
    public String goalDetail(@AuthenticationPrincipal SecurityUser user, @PathVariable("goalId") int goalId, Model model) {
        model.addAttribute("goal", exerciseGoalService.getUserGoalByUserId(user.getUser().getUserId(), goalId));
        model.addAttribute("condition", exerciseConditionService.getConditionLevelByUserId(user.getUser().getUserId()));
        model.addAttribute("targetParts", targetPartsMapper.getTargetPartsByUserId(user.getUser().getUserId()));
        model.addAttribute("avoidParts", avoidPartsMapper.getAvoidPartsByUserId(user.getUser().getUserId()));
        return "exerciseGoal/GoalDetail";
    }
}
