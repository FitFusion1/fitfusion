package com.fitfusion.service;

import com.fitfusion.dto.ExerciseItemDto;
import com.fitfusion.dto.RoutineDetailDto;
import com.fitfusion.dto.RoutineListDto;
import com.fitfusion.mapper.RoutineExerciseMapper;
import com.fitfusion.mapper.RoutineMapper;
import com.fitfusion.vo.RecommendedExercise;
import com.fitfusion.vo.Routine;
import com.fitfusion.vo.RoutineExercise;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoutineService {

    private final RoutineMapper routineMapper;
    private final RoutineExerciseMapper routineExerciseMapper;


    @Transactional
    public int saveRecommendedRoutine(int userId, List<RecommendedExercise> exercises) {
        Routine routine = new Routine();
        int routineId = routineMapper.getNextRoutineId();

        routine.setRoutineId(routineId);
        routine.setUserId(userId);
        routine.setName("AI 추천 루틴");
        routine.setDifficultyLevel("중간");
        routine.setCreatedDate(new Date());
        routine.setUpdatedDate(new Date());
        routine.setDescription("AI 자동 생성 루틴");

        routineMapper.insertRoutine(routine);

        for (RecommendedExercise ex : exercises) {
            RoutineExercise routineEx = new RoutineExercise();
            routineEx.setRoutineId(routineId);
            routineEx.setExerciseId(ex.getExerciseId());
            routineEx.setSets(ex.getSets());
            routineEx.setReps(ex.getReps());
            routineEx.setWeight(ex.getWeight());

            routineExerciseMapper.insertRoutineExerCise(routineEx);
        }

        return routineId;
    }

    public List<RoutineListDto> getRoutineListByUserId(int userId) {
        return routineMapper.getRoutineListByUserId(userId);
    }

    public List<RoutineListDto> getRoutineByUserAndRoutineId(int userId, int routineId) {
        return routineMapper.selectRoutineByUserAndRoutineId(userId, routineId);
    }

    public List<RoutineListDto> getRoutineByUserId(int userId) {
        return routineMapper.getRoutineListByUserId(userId);
    }

    public void deleteRoutineListByUserAndRoutine(int userId, int routineId) {
        routineMapper.deleteRoutineByUserAndRoutineId(userId, routineId);
    }
    public void updateRoutine(Routine routine) {
        routineMapper.updateRoutine(routine);
    }

    public Routine getRoutineDetailByUserId(int userId, int routineId) {
        return routineMapper.getRoutineDetailByUserAndRoutineId(userId, routineId);
    }

    public RoutineDetailDto getRoutineDetail(int routineId) {
        RoutineDetailDto routine = routineMapper.getRoutineInfo(routineId);
        List<ExerciseItemDto> exercises = routineMapper.selectRoutineExercises(routineId);
        routine.setRoutineId(routineId);
        routine.setExercises(exercises);
        routine.setTotalExercises(exercises.size());

        return routine;
    }

    @Transactional
    public void saveCustomRoutine(int userId, RoutineDetailDto routineDto) {
        int routineId = routineMapper.getNextRoutineId();

        Routine routine = new Routine();
        routine.setRoutineId(routineId);
        routine.setUserId(userId);
        routine.setName(routineDto.getRoutineName());
        routine.setDescription(routineDto.getDescription());

        routineMapper.insertRoutine(routine);

        for (ExerciseItemDto ex : routineDto.getExercises()) {
            RoutineExercise routineEx = new RoutineExercise();
            routineEx.setRoutineId(routineId);
            routineEx.setExerciseId(ex.getExerciseId());
            routineEx.setSets(ex.getSets());
            routineEx.setReps(ex.getReps());

            routineExerciseMapper.insertRoutineExerCise(routineEx);
        }
    }

    @Transactional
    public void updateCustomRoutine(int userId, RoutineDetailDto routineDto) {
        System.out.println("받은 routineDto: " + routineDto);
        System.out.println("운동 리스트: " + routineDto.getExercises());


        Routine routine = new Routine();

        routine.setRoutineId(routineDto.getRoutineId());
        routine.setUserId(userId);
        routine.setName(routineDto.getRoutineName());
        routine.setDescription(routineDto.getDescription());

        routineMapper.updateRoutine(routine);

        routineExerciseMapper.deleteRoutineExercisesByRoutineId(routineDto.getRoutineId());

        for (ExerciseItemDto ex : routineDto.getExercises()) {
            RoutineExercise routineEx = new RoutineExercise();
            routineEx.setRoutineId(routineDto.getRoutineId());
            routineEx.setExerciseId(ex.getExerciseId());
            routineEx.setSets(ex.getSets());
            routineEx.setReps(ex.getReps());

            routineExerciseMapper.insertRoutineExerCise(routineEx);
        }
    }
}
