package com.fitfusion.mapper;

import com.fitfusion.dto.ExerciseItemDto;
import com.fitfusion.dto.RoutineDetailDto;
import com.fitfusion.dto.RoutineListDto;
import com.fitfusion.vo.Routine;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoutineMapper {
    void insertRoutine(Routine routine);

    int getNextRoutineId();

    List<RoutineListDto> getRoutineListByUserId(int userId);

    List<RoutineListDto> selectRoutineByUserAndRoutineId(int userId, int routineId);

    void deleteRoutineByUserAndRoutineId(int userId, int routineId);

    void updateRoutine(Routine routine);

    Routine getRoutineDetailByUserAndRoutineId(int userId, int routineId);

    RoutineDetailDto getRoutineInfo(int routineId);

    List<ExerciseItemDto> selectRoutineExercises(int routineId);
}
