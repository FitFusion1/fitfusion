package com.fitfusion.mapper;

import com.fitfusion.vo.RoutineExercise;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RoutineExerciseMapper {

    void insertRoutineExerCise(RoutineExercise routineEx);

    void deleteRoutineExercisesByRoutineId(int routineId);
}
