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

    /**
     * Get coaching workout history for a specific user
     * @param userId the user ID
     * @return list of coaching history DTOs ordered by date (newest first)
     */
    public List<CoachingHistoryDto> getCoachingHistoryByUserId(int userId) {
        return coachingLogMapper.getCoachingHistoryByUserId(userId);
    }

    /**
     * Get detailed coaching workout information by log ID
     * @param id the coaching log ID
     * @return coaching history DTO with complete details
     */
    public CoachingHistoryDto getCoachingHistoryById(int id) {
        return coachingLogMapper.getCoachingHistoryById(id);
    }

    /**
     * Get coaching workout statistics for a user
     * @param userId the user ID
     * @return coaching statistics summary
     */
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
                .totalDurationMinutes(totalDuration / 60)
                .build();
    }

    /**
     * Inner class for coaching statistics summary
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CoachingStatsSummary {
        private int totalWorkouts;
        private int completedWorkouts;
        private double averageAccuracy;
        private int totalDurationMinutes;

        public static Builder builder() {
            return new Builder();
        }

        public String getFormattedAverageAccuracy() {
            return String.format("%.1f%%", averageAccuracy);
        }

        public String getFormattedTotalDuration() {
            int hours = totalDurationMinutes / 60;
            int minutes = totalDurationMinutes % 60;
            if (hours > 0) {
                return String.format("%d시간 %d분", hours, minutes);
            } else {
                return String.format("%d분", minutes);
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
            private int totalDurationMinutes;

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

            public Builder totalDurationMinutes(int totalDurationMinutes) {
                this.totalDurationMinutes = totalDurationMinutes;
                return this;
            }

            public CoachingStatsSummary build() {
                return new CoachingStatsSummary(totalWorkouts, completedWorkouts,
                        averageAccuracy, totalDurationMinutes);
            }
        }
    }
}
