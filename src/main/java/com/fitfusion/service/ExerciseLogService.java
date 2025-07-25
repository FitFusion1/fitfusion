package com.fitfusion.service;

import com.fitfusion.dto.ExerciseLogDto;
import com.fitfusion.dto.RoutineLogDto;
import com.fitfusion.mapper.ExerciseLogMapper;
import com.fitfusion.mapper.RoutineMapper;
import com.fitfusion.vo.ExerciseLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExerciseLogService {

    private final ExerciseLogMapper exerciseLogMapper;
    private final int userId = 1;
    private final RoutineMapper routineMapper;


    public void saveExerciseLog(ExerciseLog log) {
        exerciseLogMapper.insertExerciseLog(log);
    }
    
    public List<ExerciseLogDto> getExerciseLogByUserId(int userId) {
        return exerciseLogMapper.findExerciseLog(userId);
    }

    public List<RoutineLogDto> getRoutineLogByUserId(int userId) {
        List<ExerciseLogDto> exerciseLogs = exerciseLogMapper.findRoutineLogDetail(userId);
        Map<Integer, RoutineLogDto> routineLogs = new LinkedHashMap<>();

        for (ExerciseLogDto log : exerciseLogs) {
            int routineId = log.getRoutineId();

            RoutineLogDto routineLogDto = routineLogs.get(routineId);
            if (routineLogDto == null) {
                routineLogDto = new RoutineLogDto();
                routineLogDto.setRoutineId(routineId);
                routineLogDto.setRoutineName(log.getRoutineName());
                routineLogDto.setLogDate(log.getLogDate());
                routineLogDto.setExerciseLogs(new ArrayList<>());
                routineLogs.put(routineId, routineLogDto);
            }

            routineLogDto.getExerciseLogs().add(log);
        }

        return new ArrayList<>(routineLogs.values());
    }


}
