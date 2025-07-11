package com.fitfusion.service;

import com.fitfusion.vo.ExerciseGoal;
import com.fitfusion.mapper.ExerciseGoalMapper;
import com.fitfusion.web.form.ExerciseGoalRegisterForm;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ExerciseGoalService {

    @Autowired
    private ExerciseGoalMapper exerciseGoalMapper;
    @Autowired
    private ModelMapper modelMapper;


    public ExerciseGoalService(ExerciseGoalMapper exerciseGoalMapper) {
        this.exerciseGoalMapper = exerciseGoalMapper;
    }

    public void insertUserGoal(ExerciseGoalRegisterForm form) {
        ExerciseGoal exerciseGoal = modelMapper.map(form, ExerciseGoal.class);
        exerciseGoal.setGoalType(exerciseGoal.getGoalType());
        exerciseGoal.setStartWeight(exerciseGoal.getStartWeight());
        exerciseGoal.setTargetWeight(exerciseGoal.getTargetWeight());
        exerciseGoal.setGoalDescription(exerciseGoal.getGoalDescription());

        exerciseGoalMapper.insertUserGoal(exerciseGoal);
    }
}
