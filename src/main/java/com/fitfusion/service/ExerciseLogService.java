package com.fitfusion.service;

import com.fitfusion.dto.ExerciseLogDto;
import com.fitfusion.dto.RoutineLogDto;
import com.fitfusion.mapper.ExerciseLogMapper;
import com.fitfusion.mapper.RoutineMapper;
import com.fitfusion.vo.ExerciseLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class ExerciseLogService {

    private final ExerciseLogMapper exerciseLogMapper;
    private final RoutineMapper routineMapper;


    public RoutineLogDto getRoutineLogDetailByUserIdAndRoutineId(int userId, int routineId, int sessionId) {
        RoutineLogDto routineLogDto = exerciseLogMapper.getRoutineInfo(routineId);
        List<ExerciseLogDto> logs = exerciseLogMapper.getRoutineLogDetail(userId, routineId, sessionId);
        routineLogDto.setExerciseLogs(logs);

        return routineLogDto;
    }

    public void deleteExerciseLog(int userId, int sessionId) {
        exerciseLogMapper.deleteExerciseLogByUserAndLogId(userId, sessionId);
    }

    public void saveExerciseLog(ExerciseLog log) {
        exerciseLogMapper.insertExerciseLog(log);
    }

    public int startRoutine(int routineId, int userId, List<ExerciseLogDto> exerciseLogs) {
        int sessionId = exerciseLogMapper.getNextSessionId();
        for (ExerciseLogDto logDto : exerciseLogs) {
            ExerciseLog log = new ExerciseLog();
            log.setUserId(userId);
            log.setRoutineExerciseId(logDto.getRoutineExerciseId());
            log.setExerciseId(logDto.getExerciseId());
            log.setSessionId(sessionId);
            log.setSets(0);
            log.setReps(0);
            log.setIsChecked("N");
            log.setDurationMinutes(0);
            saveExerciseLog(log);
        }
        return sessionId;
    }


    public void saveRoutineLog(int userId, RoutineLogDto routineDto) {
        int sessionId = exerciseLogMapper.getNextSessionId();
        for (ExerciseLogDto dto : routineDto.getExerciseLogs()) {
           ExerciseLog log = new ExerciseLog();
           log.setUserId(userId);
           log.setRoutineExerciseId(dto.getRoutineExerciseId());
           log.setExerciseId(dto.getExerciseId());
           log.setSessionId(sessionId);

           Integer sets = dto.getSets();
           Integer reps = dto.getReps();
           Integer duration = dto.getDurationMinutes();
           String isChecked = (dto.getIsChecked() != null && dto.getIsChecked().equals("Y")) ? "Y" : "N";

           boolean hasPartialInput = (sets != null && sets > 0) || (reps != null && reps > 0);

           if (hasPartialInput) {
               log.setSets(sets != null ? sets : 0);
               log.setReps(reps != null ? reps : 0);
               log.setIsChecked("Y");
           } else if ("Y".equals(isChecked)) {
               log.setSets(dto.getRecommendedSets() != null ? dto.getRecommendedSets() : 0);
               log.setReps(dto.getRecommendedReps() != null ? dto.getRecommendedReps() : 0);
               log.setIsChecked("Y");
           } else {
               log.setSets(0);
               log.setReps(0);
               log.setIsChecked("N");
           }

           log.setDurationMinutes(duration != null ? duration : 0);

           saveExerciseLog(log);
        }
    }
    
    public List<ExerciseLogDto> getExerciseLogsByUserId(int userId) {
        return exerciseLogMapper.findExerciseLog(userId);
    }


    public List<RoutineLogDto> getRoutineLogsByUserId(int userId) {
        List<ExerciseLogDto> exerciseLogs = exerciseLogMapper.findRoutineLogsDetail(userId);
        Map<String, RoutineLogDto> routineLogs = new LinkedHashMap<>();

        for (ExerciseLogDto log : exerciseLogs) {
            String key = log.getRoutineId() + "-" + log.getSessionId();
            int routineId = log.getRoutineId();
            RoutineLogDto routineLogDto = routineLogs.get(key);

            if (routineLogDto == null) {
                routineLogDto = new RoutineLogDto();
                routineLogDto.setLogId(log.getLogId());
                routineLogDto.setRoutineId(routineId);
                routineLogDto.setRoutineName(log.getRoutineName());
                routineLogDto.setLogDate(log.getLogDate());
                routineLogDto.setSessionId(log.getSessionId());
                routineLogDto.setExerciseLogs(new ArrayList<>());
                routineLogs.put(key, routineLogDto);
            }
            routineLogDto.getExerciseLogs().add(log);
        }

        return new ArrayList<>(routineLogs.values());
    }


    public void updateLog(RoutineLogDto routineLog, int userId) {
        for (ExerciseLogDto exerciseLog : routineLog.getExerciseLogs()) {
            exerciseLog.setLogDate(routineLog.getLogDate());
            exerciseLog.setUserId(userId);
            exerciseLogMapper.updateExerciseLog(exerciseLog);
        }
    }

}
