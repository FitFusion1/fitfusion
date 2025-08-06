package com.fitfusion.mapper;

import com.fitfusion.dto.ExerciseItemDto;
import com.fitfusion.dto.RoutineDetailDto;
import com.fitfusion.dto.RoutineDto;
import com.fitfusion.dto.RoutineListDto;
import com.fitfusion.vo.Routine;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoutineMapper {

    void insertRoutine(@Param("routine") Routine routine);

    int getNextRoutineId();

    List<RoutineListDto> getRoutineListByUserId(@Param("userId") int userId, @Param("offset") int offset, @Param("size") int size);

    List<RoutineListDto> selectRoutineByUserAndRoutineId(@Param("userId") int userId, @Param("routineId") int routineId);

    void deleteRoutineByUserAndRoutineId(@Param("userId") int userId, @Param("routineId") int routineId);

    void updateRoutine(@Param("routine") Routine routine);

    Routine getRoutineDetailByUserAndRoutineId(@Param("userId") int userId, @Param("routineId") int routineId);

    RoutineDetailDto getRoutineInfo(@Param("routineId") int routineId, @Param("userId") int userId);

    List<ExerciseItemDto> selectRoutineExercises(@Param("routineId") int routineId, @Param("userId") int userId);

    List<RoutineDto> selectLatestRoutinesByUser(@Param("userId") int userId);

    long countRoutineByUserId(@Param("userId") int userId);
}
