package com.fitfusion.web.controller;

import com.fitfusion.dto.CoachingExerciseDto;
import com.fitfusion.dto.CoachingHistoryDto;
import com.fitfusion.security.SecurityUser;
import com.fitfusion.service.CoachingExerciseService;
import com.fitfusion.service.CoachingHistoryService;
import com.fitfusion.web.form.LiveCoachingForm;
import com.fitfusion.mapper.CoachingFeedbackMapper;
import com.fitfusion.vo.CoachingFeedback;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/live-coaching")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class LiveCoachingController {

    @Autowired
    private final CoachingExerciseService coachingExerciseService;

    @Autowired
    private final CoachingHistoryService coachingHistoryService;
    
    @Autowired
    private final CoachingFeedbackMapper coachingFeedbackMapper;

    @GetMapping
    public String selectExerciseForm(Model model) {
        LiveCoachingForm liveCoachingForm = new LiveCoachingForm();
        liveCoachingForm.setTargetSets(3);
        liveCoachingForm.setTargetReps(10);
        liveCoachingForm.setTargetTime(30);
        List<CoachingExerciseDto> exerciseDtoList = coachingExerciseService.getCoachingExercises();
        model.addAttribute("liveCoachingForm", liveCoachingForm);
        model.addAttribute("exerciseList", exerciseDtoList);
        model.addAttribute("exerciseListJson", coachingExerciseService.convertExercisesToJson(exerciseDtoList));
        return "live-coaching/exercise-select";
    }

    @PostMapping("/exercise-feedback")
    public String startLiveCoachingSession(@Valid @ModelAttribute LiveCoachingForm form,
                                           BindingResult errors,
                                           Model model) {
        if (errors.hasErrors()) {
            return "live-coaching/exercise-select";
        }
        form.setExerciseName(coachingExerciseService.getExerciseNameById(form.getExerciseId()));
        form.setExerciseTitle(coachingExerciseService.getExerciseTitleById(form.getExerciseId()));
        model.addAttribute("liveCoachingForm", form);
        return "live-coaching/exercise-feedback";
    }

    @GetMapping("/history")
    public String coachingHistory(Model model, @AuthenticationPrincipal SecurityUser user) {
        int userId = user.getUser().getUserId();
        List<CoachingHistoryDto> historyList = coachingHistoryService.getCoachingHistoryByUserId(userId);
        CoachingHistoryService.CoachingStatsSummary stats = coachingHistoryService.getCoachingStats(userId);

        model.addAttribute("historyList", historyList);
        model.addAttribute("stats", stats);

        return "live-coaching/history";
    }

    @GetMapping("/history/{id}")
    public String coachingHistoryDetail(@PathVariable("id") int id, Model model,
                                        @AuthenticationPrincipal SecurityUser user) {
        CoachingHistoryDto historyDetail = coachingHistoryService.getCoachingHistoryById(id);

        if (historyDetail == null) {
            return "redirect:/live-coaching/history";
        }

        if (historyDetail.getUserId() != user.getUser().getUserId()) {
            return "redirect:/live-coaching/history";
        }

        List<CoachingFeedback> feedbackList = coachingFeedbackMapper.selectFeedbackByCoachingLogId(id);

        model.addAttribute("historyDetail", historyDetail);
        model.addAttribute("feedbackList", feedbackList);

        return "live-coaching/detail";
    }

}
