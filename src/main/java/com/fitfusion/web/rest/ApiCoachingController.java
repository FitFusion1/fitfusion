package com.fitfusion.web.rest;

import com.fitfusion.dto.CoachingFeedbackDto;
import com.fitfusion.exception.AppException;
import com.fitfusion.security.SecurityUser;
import com.fitfusion.service.CoachingExerciseService;
import com.fitfusion.vo.CoachingLog;
import com.fitfusion.web.response.ApiResponse;
import com.fitfusion.web.response.ResponseEntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/live-coaching")
public class ApiCoachingController {

    @Autowired
    private CoachingExerciseService coachingExerciseService;

    @PostMapping("/save-session")
    public ResponseEntity<ApiResponse<Integer>> saveCoachingSession(@RequestBody CoachingLog coachingLog,
                                                                    @AuthenticationPrincipal SecurityUser user) {
        coachingLog.setUserId(user.getUser().getUserId());
        try {
            coachingExerciseService.saveCoachingSession(coachingLog);
        } catch (AppException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AppException(ex.getMessage(), ex);
        }

        return ResponseEntityUtils.ok("세션을 성공적으로 저장했습니다.", coachingLog.getId());
    }

    @PostMapping("/save-feedback")
    public ResponseEntity<ApiResponse<Void>> saveCoachingFeedback(@RequestBody CoachingFeedbackDto feedbackDto) {
        coachingExerciseService.saveCoachingFeedback(feedbackDto.getCoachingLogId(), feedbackDto.getFeedbackList());
        return ResponseEntityUtils.ok("피드백 저장 완료");
    }
}
