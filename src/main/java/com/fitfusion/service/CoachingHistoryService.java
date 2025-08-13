package com.fitfusion.service;

import com.fitfusion.dto.CoachingHistoryDto;
import com.fitfusion.mapper.CoachingLogMapper;
import lombok.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CoachingHistoryService {

    private final CoachingLogMapper coachingLogMapper;

    public List<CoachingHistoryDto> getCoachingHistoryByUserId(int userId) {
        return coachingLogMapper.getCoachingHistoryByUserId(userId);
    }

    public CoachingHistoryDto getCoachingHistoryById(int id) {
        return coachingLogMapper.getCoachingHistoryById(id);
    }

    public CoachingStatsSummary getCoachingStats(int userId) {
        List<CoachingHistoryDto> history = getCoachingHistoryByUserId(userId);

        if (history.isEmpty()) {
            return new CoachingStatsSummary();
        }

        int totalWorkouts = history.size();
        double avgAccuracy = history.stream()
                .mapToDouble(CoachingHistoryDto::getAccuracyPercent)
                .average()
                .orElse(0.0);

        int totalDuration = history.stream()
                .mapToInt(CoachingHistoryDto::getDuration)
                .sum();

        long completedWorkouts = history.stream()
                .filter(h -> "완료".equals(h.getCompletionStatus()))
                .count();

        return CoachingStatsSummary.builder()
                .totalWorkouts(totalWorkouts)
                .completedWorkouts((int) completedWorkouts)
                .averageAccuracy(avgAccuracy)
                .totalDurationSeconds(totalDuration)
                .build();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CoachingStatsSummary {
        private int totalWorkouts;
        private int completedWorkouts;
        private double averageAccuracy;
        private int totalDurationSeconds;

        public static Builder builder() {
            return new Builder();
        }

        public String getFormattedAverageAccuracy() {
            return String.format("%.1f%%", averageAccuracy);
        }

        public String getFormattedTotalDuration() {
            int hours = totalDurationSeconds / 360;
            int minutes = totalDurationSeconds / 60;
            int seconds = totalDurationSeconds % 60;
            if (hours > 0) {
                return String.format("%d시간 %d분 %d초", hours, minutes, seconds);
            } else if (minutes > 0) {
                return String.format("%d분 %d초", minutes, seconds);
            } else {
                return String.format("%d초", seconds);
            }
        }

        public double getCompletionRate() {
            return totalWorkouts > 0 ? (double) completedWorkouts / totalWorkouts * 100 : 0.0;
        }

        public String getFormattedCompletionRate() {
            return String.format("%.1f%%", getCompletionRate());
        }

        public static class Builder {
            private int totalWorkouts;
            private int completedWorkouts;
            private double averageAccuracy;
            private int totalDurationSeconds;

            public Builder totalWorkouts(int totalWorkouts) {
                this.totalWorkouts = totalWorkouts;
                return this;
            }

            public Builder completedWorkouts(int completedWorkouts) {
                this.completedWorkouts = completedWorkouts;
                return this;
            }

            public Builder averageAccuracy(double averageAccuracy) {
                this.averageAccuracy = averageAccuracy;
                return this;
            }

            public Builder totalDurationSeconds(int totalDurationSeconds) {
                this.totalDurationSeconds = totalDurationSeconds;
                return this;
            }

            public CoachingStatsSummary build() {
                return new CoachingStatsSummary(totalWorkouts, completedWorkouts,
                        averageAccuracy, totalDurationSeconds);
            }
        }
    }
}
