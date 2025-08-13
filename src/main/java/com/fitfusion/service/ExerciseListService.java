package com.fitfusion.service;

import com.fitfusion.mapper.ExerciseListMapper;
import com.fitfusion.vo.CoachingExercise;
import com.fitfusion.vo.Exercise;
import com.fitfusion.vo.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExerciseListService {

    @Autowired
    private ExerciseListMapper exerciseListMapper;

    public int countExercises() {
        return exerciseListMapper.countExercises();
    }

    public int countCoachingExercises() {
        return exerciseListMapper.countCoachingExercises();
    }

    public int getCategoryCount() {
        return exerciseListMapper.getCategoryCount();
    }

    public int getEquipmentCount() {
        return exerciseListMapper.getEquipmentCount();
    }

    public List<Exercise> getAllExercises() {
        return exerciseListMapper.getAllExercises();
    }

    public List<CoachingExercise> getAllCoachingExercises() {
        return exerciseListMapper.getAllCoachingExercises();
    }

    public Exercise getExerciseById(int id) {
        return exerciseListMapper.getExerciseById(id);
    }

    public Video getLatestByExerciseId(int exerciseId) {
        return exerciseListMapper.getLatestByExerciseId(exerciseId);
    }
}
