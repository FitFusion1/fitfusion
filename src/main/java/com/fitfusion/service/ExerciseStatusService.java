package com.fitfusion.service;

import com.fitfusion.mapper.ExerciseStatusMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ExerciseStatusService {

    private final ExerciseStatusMapper exerciseStatusMapper;

    public Map<String, Object> getExerciseStatus(int userId) {
        Map<String, Object> result = new HashMap<>();

        /** ✅ 1. 이번 달 총 운동 시간 */
        int totalMinutes = exerciseStatusMapper.getMonthlyTotalTime(userId);
        String formattedTime = (totalMinutes / 60) + "시간 " + (totalMinutes % 60) + "분";
        result.put("monthlyTotalTime", formattedTime);

        /** ✅ 2. 루틴 기반 성실도 */
        int totalRoutineCount = exerciseStatusMapper.getTotalRoutineCount(userId);
        int completedSessions = exerciseStatusMapper.getCompletedRoutineSessions(userId);
        int consistencyScore = (totalRoutineCount > 0) ? (completedSessions * 100) / totalRoutineCount : 0;

        result.put("totalRoutineCount", totalRoutineCount);
        result.put("completedSessions", completedSessions);
        result.put("consistencyScore", consistencyScore);

        /** ✅ 3. 평균 운동 완료율 */
        double avgCompletionRate = exerciseStatusMapper.getAverageCompletionRate(userId);
        result.put("averageCompletionRate", Math.round(avgCompletionRate));

        /** ✅ 4. 가장 많이/적게 한 부위 */
        result.put("mostUsedPart", Optional.ofNullable(exerciseStatusMapper.getMostUsedBodyPart(userId)).orElse("없음"));
        result.put("leastUsedPart", Optional.ofNullable(exerciseStatusMapper.getLeastUsedBodyPart(userId)).orElse("없음"));

        /** ✅ 5. 이번 주 요일별 루틴 수행 횟수 */
        List<String> dailyLabels = Arrays.asList("월","화","수","목","금","토","일");
        List<Integer> dailyData = new ArrayList<>(Collections.nCopies(7, 0));
        for (Map<String, Object> row : exerciseStatusMapper.getDailyRoutineCount(userId)) {
            String day = row.get("DAY").toString().trim();
            int count = ((Number) row.get("COUNT")).intValue();
            int index = dailyLabels.indexOf(day);
            if (index >= 0) dailyData.set(index, count);
        }
        result.put("dailyLabels", dailyLabels);
        result.put("dailyData", dailyData);

        /** ✅ 6. 운동 부위 비율 */
        List<Map<String, Object>> balanceRaw = exerciseStatusMapper.getBalanceParts(userId);
        List<String> balanceLabels = new ArrayList<>();
        List<Integer> balanceData = new ArrayList<>();
        int total = balanceRaw.stream().mapToInt(row -> ((Number) row.get("CNT")).intValue()).sum();
        for (Map<String, Object> row : balanceRaw) {
            String part = row.get("PART").toString();
            int cnt = ((Number) row.get("CNT")).intValue();
            int percent = total > 0 ? (cnt * 100 / total) : 0;
            balanceLabels.add(part);
            balanceData.add(percent);
        }
        result.put("balanceLabels", balanceLabels);
        result.put("balanceData", balanceData);

        /** ✅ 7. 퍼스널 레코드 */
        result.put("personalRecords", exerciseStatusMapper.getPersonalRecords(userId));

        return result;
    }
}
