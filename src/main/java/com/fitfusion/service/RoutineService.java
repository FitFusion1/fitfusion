package com.fitfusion.service;

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
}
