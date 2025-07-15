package com.fitfusion.repository;

import com.fitfusion.vo.Exercise;

import java.util.List;

public interface ExerciseRepository {
    List<Exercise> findAll();
}
