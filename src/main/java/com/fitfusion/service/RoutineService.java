package com.fitfusion.service;

import com.fitfusion.dto.ExerciseItemDto;
import com.fitfusion.dto.RoutineDetailDto;
import com.fitfusion.dto.RoutineDto;
import com.fitfusion.dto.RoutineListDto;
import com.fitfusion.enums.BodyPart;
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


    public List<RoutineDto> getRecentRoutine(int userId) {
        return routineMapper.selectLatestRoutinesByUser(userId);
    }

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

    public List<RoutineListDto> getRoutineListByUserId(int userId, int page, int size) {
        int offset = (page - 1) * size;
        return routineMapper.getRoutineListByUserId(userId, offset, size);
    }

    public List<RoutineListDto> getRoutineByUserAndRoutineId(int userId, int routineId) {
        return routineMapper.selectRoutineByUserAndRoutineId(userId, routineId);
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

    public RoutineDetailDto getRoutineDetail(int routineId, int userId) {
        RoutineDetailDto routine = routineMapper.getRoutineInfo(routineId, userId);
        List<ExerciseItemDto> exercises = routineMapper.selectRoutineExercises(routineId, userId);
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

    public int saveTargetRoutine(int userId, String bodyPart, List<RecommendedExercise> exercises) {

        int routineId = routineMapper.getNextRoutineId();

        Routine routine = Routine.builder()
                .routineId(routineId)
                .userId(userId)
                .name(BodyPart.valueOf(bodyPart).getBodyName() + " 맞춤 루틴")
                .difficultyLevel("중간")
                .description("부족 부위 보완 루틴")
                .createdDate(new Date())
                .updatedDate(new Date())
                .build();
        routineMapper.insertRoutine(routine);

        for (RecommendedExercise re : exercises) {
            RoutineExercise rel = RoutineExercise.builder()
                    .routineId(routineId)
                    .exerciseId(re.getExerciseId())
                    .sets(re.getSets())
                    .reps(re.getReps())
                    .weight(re.getWeight())
                    .build();
            routineExerciseMapper.insertRoutineExerCise(rel);
        }
        return routineId;
    }

    public long countRoutineByUserId(int userId) {
        return routineMapper.countRoutineByUserId(userId);
    }
}
