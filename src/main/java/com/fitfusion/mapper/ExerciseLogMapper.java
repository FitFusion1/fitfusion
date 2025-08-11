package com.fitfusion.mapper;

import com.fitfusion.dto.ExerciseLogDto;
import com.fitfusion.dto.RoutineLogDto;
import com.fitfusion.vo.ExerciseLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExerciseLogMapper {

    void deleteExerciseLogByUserAndLogId(@Param("userId") int userId, @Param("sessionId") int sessionId);

    void insertExerciseLog(@Param("log") ExerciseLog log);

    List<ExerciseLogDto> findExerciseLog(@Param("userId") int userId);

    List<ExerciseLogDto> findExerciseLogsDetail(@Param("userId") int userId, @Param("offset") int offset, @Param("size") int size);

    List<ExerciseLogDto> findAllExerciseLogsDetail(@Param("userId") int userId);

    int getNextSessionId();

    RoutineLogDto getRoutineInfo(@Param("routineId") int routineId);

    List<ExerciseLogDto> getRoutineLogDetail(@Param("userId") int userId, @Param("routineId") int routineId, @Param("sessionId") int sessionId);

    void updateExerciseLog(@Param("exerciseLog") ExerciseLogDto exerciseLog);

    long countDistinctSessionByUserId(@Param("userId") int userId);
}
