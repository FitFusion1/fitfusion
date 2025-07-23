package com.fitfusion.service;

import com.fitfusion.dto.ExerciseLogDto;
import com.fitfusion.mapper.ExerciseLogMapper;
import com.fitfusion.vo.ExerciseLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExerciseLogService {

    private final ExerciseLogMapper exerciseLogMapper;
    private final int userId = 1;


    public void saveExerciseLog(ExerciseLog log) {
        exerciseLogMapper.insertExerciseLog(log);
    }
    
    public List<ExerciseLogDto> getExerciseLogByUserId(int userId) {
        return exerciseLogMapper.findExerciseLog(userId);
    }

}
