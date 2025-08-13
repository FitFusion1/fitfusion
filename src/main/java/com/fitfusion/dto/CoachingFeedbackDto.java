package com.fitfusion.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CoachingFeedbackDto {
    private int coachingLogId;
    private List<FeedbackItem> feedbackList;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class FeedbackItem {
        private String issueCode;
        private String description;
        private int setNo;
        private int repNo;
        private int timestamp;
    }
}
