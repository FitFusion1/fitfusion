package com.fitfusion.mapper;

import com.fitfusion.dto.ExerciseLogDto;
import com.fitfusion.dto.RoutineLogDto;
import com.fitfusion.vo.ExerciseLog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ExerciseLogMapper {

    void deleteExerciseLogByUserAndLogId(int userId, int sessionId);

    void insertExerciseLog(ExerciseLog log);

    List<ExerciseLogDto> findExerciseLog(int userId);

    List<ExerciseLogDto> findRoutineLogsDetail(int userId);

    int getNextSessionId();

    RoutineLogDto getRoutineInfo(int routineId);

    List<ExerciseLogDto> getRoutineLogDetail(int userId, int routineId, int sessionId);

    void updateExerciseLog(ExerciseLogDto exerciseLog);

    void updateRoutineName(RoutineLogDto routineLog);
}
