package com.fitfusion.service;

import com.fitfusion.dto.ExerciseLogDto;
import com.fitfusion.dto.RoutineLogDto;
import com.fitfusion.mapper.ExerciseLogMapper;
import com.fitfusion.vo.ExerciseLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class ExerciseLogService {

    private final ExerciseLogMapper exerciseLogMapper;



    public Long countLogDistinctSessionByUserId(int userId) {
        return exerciseLogMapper.countDistinctSessionByUserId(userId);
    }

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
           Integer weight = dto.getWeight();
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
           log.setWeight(weight != null ? weight : 0);


           saveExerciseLog(log);
        }
    }
    
    public List<ExerciseLogDto> getFourExerciseLogsByUserId(int userId) {
        return exerciseLogMapper.findExerciseLog(userId);
    }


    public List<RoutineLogDto> getExerciseLogsByUserId(int userId, int page, int size) {
        List<ExerciseLogDto> exerciseLogs = exerciseLogMapper.findAllExerciseLogsDetail(userId);
        Map<String, RoutineLogDto> routineLogs = new LinkedHashMap<>();

        for (ExerciseLogDto log : exerciseLogs) {
            String key = log.getRoutineId() + "-" + log.getSessionId();
            routineLogs.computeIfAbsent(key, k -> {
                RoutineLogDto routineLogDto = new RoutineLogDto();
                routineLogDto.setLogId(log.getLogId());
                routineLogDto.setRoutineId(log.getRoutineId());
                routineLogDto.setRoutineName(log.getRoutineName());
                routineLogDto.setLogDate(log.getLogDate());
                routineLogDto.setSessionId(log.getSessionId());
                routineLogDto.setExerciseLogs(new ArrayList<>());
                return routineLogDto;
            }).getExerciseLogs().add(log);
        }

        List<RoutineLogDto> routines = new ArrayList<>(routineLogs.values());

        int from = (page - 1) * size;
        if (from >= routines.size()) {
            return Collections.emptyList();
        }
        int to = Math.min(from + size, routines.size());
        return routines.subList(from, to);
    }


    public void updateLog(RoutineLogDto routineLog, int userId) {
        for (ExerciseLogDto exerciseLog : routineLog.getExerciseLogs()) {
            exerciseLog.setLogDate(routineLog.getLogDate());
            exerciseLog.setUserId(userId);
            exerciseLogMapper.updateExerciseLog(exerciseLog);
        }
    }

}
