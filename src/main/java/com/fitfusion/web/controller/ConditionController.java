package com.fitfusion.web.controller;

import com.fitfusion.enums.BodyPart;
import com.fitfusion.enums.ConditionLevel;
import com.fitfusion.service.ExerciseConditionService;
import com.fitfusion.web.form.ExerciseConditionForm;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/condition")
public class ConditionController {

    private final int userId = 1;
    private final ExerciseConditionService conditionService;

    @GetMapping("/save")
    public String saveConditionPage(Model model) {
        List<BodyPart> bodyParts = Arrays.asList(BodyPart.values());
        List<ConditionLevel> conditionLevels = Arrays.asList(ConditionLevel.values());

        model.addAttribute("bodyParts", bodyParts);
        model.addAttribute("conditionLevels", conditionLevels);

        return "exerciseCondition/ExerciseCondition";
    }

    @PostMapping("/save")
    public String saveCondition(@ModelAttribute ExerciseConditionForm formData, HttpSession session, Model model) {
        List<String> avoidParts = formData.getAvoidParts();
        List<String> targetParts = formData.getTargetParts();
        String condition = formData.getConditionLevel();

        if (targetParts == null || targetParts.isEmpty()) {
            model.addAttribute("bodyParts", Arrays.asList(BodyPart.values()));
            model.addAttribute("conditionLevels", Arrays.asList(ConditionLevel.values()));
            model.addAttribute("errorMessage", "운동하고 싶은 부위를 한 개 이상 선택해주세요.");

            return "exerciseCondition/ExerciseCondition";
        }

        if (condition == null || condition.isEmpty()) {
            model.addAttribute("bodyParts", Arrays.asList(BodyPart.values()));
            model.addAttribute("conditionLevels", Arrays.asList(ConditionLevel.values()));
            model.addAttribute("errorMessage", "컨디션을 선택해주세요.");

            return "exerciseCondition/ExerciseCondition";
        }

        if (avoidParts != null && !avoidParts.isEmpty()) {
            for (String part : avoidParts) {
                if (targetParts.contains(part)) {
                    model.addAttribute("bodyParts", Arrays.asList(BodyPart.values()));
                    model.addAttribute("conditionLevels", Arrays.asList(ConditionLevel.values()));
                    model.addAttribute("errorMessage", "하고 싶은 부위와 피하고 싶은 부위는 겹칠 수 없습니다.");
                    return  "exerciseCondition/ExerciseCondition";
                }
            }
        }


        int conditionId = conditionService.saveConditionAndAvoidAndTartget(userId, formData.getConditionLevel(), formData.getAvoidParts(), formData.getTargetParts());

        session.setAttribute("targetParts", formData.getTargetParts());
        session.setAttribute("avoidParts", formData.getAvoidParts());
        session.setAttribute("condition", formData.getConditionLevel());

        return "redirect:/routine/create";
    }
}
