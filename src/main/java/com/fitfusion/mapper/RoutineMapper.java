package com.fitfusion.mapper;

import com.fitfusion.vo.Routine;
import com.fitfusion.vo.RoutineExercise;

public interface RoutineMapper {
    void insertRoutine(Routine routine);

    void insertRoutineExercise(RoutineExercise routineExercise);
}
