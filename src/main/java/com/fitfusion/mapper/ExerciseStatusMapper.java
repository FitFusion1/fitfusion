package com.fitfusion.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ExerciseStatusMapper {

    List<Map<String, Object>> getBalanceParts(int userId);

    int getTotalRoutineCount(int userId);

    int getCompletedRoutineSessions(int userId);

    double getAverageCompletionRate(int userId);

    /**
     * 이번 달 총 운동 시간
     */
    int getMonthlyTotalTime(int userId);

    /**
     * 이번 주 실제 수행 루틴 수
     */
    int getCompletedRoutineCount(int userId);

    /**
     * 가장 많이 한 운동 부위
     */
    String getMostUsedBodyPart(int userId);

    /**
     * 가장 적게 한 운동 부위
     */
    String getLeastUsedBodyPart(int userId);

    /**
     * 이번 주 요일별 운동 수행량
     */
    List<Map<String, Object>> getDailyRoutineCount(int userId);

    /**
     * 퍼스널 레코드
     */
    List<Map<String, Object>> getPersonalRecords(int userId);
}
