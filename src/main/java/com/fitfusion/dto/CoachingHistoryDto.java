package com.fitfusion.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.ibatis.type.Alias;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Alias("CoachingHistoryDto")
public class CoachingHistoryDto {

    private int id;
    private int userId;
    private String exerciseId;
    private String exerciseName;
    private String exerciseTitle;
    private String exerciseDescription;
    private String previewImagePath;
    private int targetSets;
    private int targetReps;
    private int performedSets;
    private int performedReps;
    private int goodReps;
    private int targetTime;
    private int performedTime;
    private int goodTime;
    private double accuracyPercent;
    private int duration;
    private Date performedDate;

    public String getFormattedDuration() {
        int minutes = duration / 60;
        int seconds = duration % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    public String getFormattedAccuracy() {
        return String.format("%.1f%%", accuracyPercent);
    }

    public boolean isTimedExercise() {
        return targetTime > 0;
    }

    public String getCompletionStatus() {
        if (isTimedExercise()) {
            return goodTime >= targetTime ? "완료" : "미완료";
        } else {
            return goodReps >= (targetReps * targetSets) ? "완료" : "미완료";
        }
    }

    public String getPerformanceRating() {
        if (accuracyPercent >= 80) {
            return "우수";
        } else if (accuracyPercent >= 60) {
            return "양호";
        } else {
            return "개선 필요";
        }
    }
}
