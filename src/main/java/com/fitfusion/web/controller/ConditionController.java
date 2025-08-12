package com.fitfusion.web.controller;

import com.fitfusion.enums.BodyPart;
import com.fitfusion.enums.ConditionLevel;
import com.fitfusion.security.SecurityUser;
import com.fitfusion.service.ExerciseConditionService;
import com.fitfusion.web.form.ExerciseConditionForm;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/condition")
@PreAuthorize("isAuthenticated()")
public class ConditionController {


    private final ExerciseConditionService exerciseConditionService;


    @GetMapping("/save")
    @PreAuthorize("isAuthenticated()")
    public String saveConditionPage(Model model) {
        modelAtt(model);
        if (!model.containsAttribute("exerciseConditionForm")){
            model.addAttribute("exerciseConditionForm", new ExerciseConditionForm());
        }
        return "exerciseCondition/ExerciseCondition";
    }


    @PostMapping("/save")
    @PreAuthorize("isAuthenticated()")
    public String saveCondition(@AuthenticationPrincipal SecurityUser user,
                                @Valid @ModelAttribute ExerciseConditionForm formData,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                HttpSession session,
                                Model model) {

        int userId = user.getUser().getUserId();
        List<String> avoidParts = formData.getAvoidParts();
        List<String> targetParts = formData.getTargetParts();
        String conditionLevel = formData.getConditionLevel();

        List<String> safeAvoid = (avoidParts == null) ? Collections.emptyList() : avoidParts;
        List<String> safeTarget = (targetParts == null) ? Collections.emptyList() : targetParts;

        if (!safeTarget.isEmpty() &&
                !safeAvoid.isEmpty() &&
                !Collections.disjoint(safeAvoid, safeTarget)) {
            bindingResult.reject("overlap", "하고 싶은 부위와 피하고 싶은 부위는 겹칠 수 없습니다.");
        }

        if (bindingResult.hasErrors()) {
            modelAtt(model);
            return "exerciseCondition/ExerciseCondition";
        }



        exerciseConditionService.saveConditionAndAvoidAndTarget(userId, conditionLevel ,safeAvoid, safeTarget);

        session.setAttribute("targetParts", safeTarget);
        session.setAttribute("avoidParts", safeAvoid);
        session.setAttribute("condition", conditionLevel);
        session.setAttribute("conditionSet", true);


        redirectAttributes.addFlashAttribute("alertMessage", "컨디션 설정이 저장되었습니다.");

        return "redirect:/routine/create/ai?conditionSet=true";
    }

    private void modelAtt(Model model) {
        model.addAttribute("bodyParts", Arrays.asList(BodyPart.values()));
        model.addAttribute("conditionLevels", Arrays.asList(ConditionLevel.values()));

    }
}
