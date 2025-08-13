package com.fitfusion.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitfusion.dto.CoachingExerciseDto;
import com.fitfusion.dto.CoachingFeedbackDto;
import com.fitfusion.exception.AppException;
import com.fitfusion.mapper.CoachingExerciseMapper;
import com.fitfusion.mapper.CoachingFeedbackMapper;
import com.fitfusion.mapper.CoachingLogMapper;
import com.fitfusion.vo.CoachingExercise;
import com.fitfusion.vo.CoachingFeedback;
import com.fitfusion.vo.CoachingLog;
import jakarta.annotation.PostConstruct;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.spi.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CoachingExerciseService {

    @Autowired
    private CoachingExerciseMapper coachingExerciseMapper;

    @Autowired
    private CoachingLogMapper coachingLogMapper;

    @Autowired
    private CoachingFeedbackMapper coachingFeedbackMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    public void initTypeMap() {
        TypeMap<CoachingExercise, CoachingExerciseDto> typeMap = modelMapper.createTypeMap(
                CoachingExercise.class, CoachingExerciseDto.class);

        typeMap.addMappings(mapper -> {
            mapper.using((MappingContext<String, List<String>> ctx) -> {
                String rawTips = ctx.getSource();
                if (rawTips == null || rawTips.isBlank()) {
                    return new ArrayList<>();
                }
                return Arrays.stream(rawTips.split("\\r?\\n"))
                        .map(String::trim)
                        .filter(t -> !t.isEmpty())
                        .collect(Collectors.toCollection(ArrayList::new));
            }).map(CoachingExercise::getTips, CoachingExerciseDto::setTips);
        });
    }

    public List<CoachingExerciseDto> getCoachingExercises() {
        List<CoachingExercise> exercises = coachingExerciseMapper.getAllCoachingExercises();

        return exercises.stream()
                .map(exercise -> modelMapper.map(exercise, CoachingExerciseDto.class))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public String convertExercisesToJson(List<CoachingExerciseDto> exerciseList) {
        try {
            Map<String, CoachingExerciseDto> exerciseMap = exerciseList.stream()
                    .collect(Collectors.toMap(CoachingExerciseDto::getTitle, Function.identity()));
            return objectMapper.writeValueAsString(exerciseMap);
        } catch (JsonProcessingException e) {
            throw new AppException(e.getMessage(), e);
        }
    }

    public void saveCoachingSession(CoachingLog coachingLog) {
        coachingLogMapper.insertCoachingLog(coachingLog);
    }

    @Transactional
    public void saveCoachingFeedback(int coachingLogId, List<CoachingFeedbackDto.FeedbackItem> feedbackList) {
        if (feedbackList == null || feedbackList.isEmpty()) return;

        feedbackList.forEach(item -> {
            CoachingFeedback feedback = new CoachingFeedback();
            feedback.setCoachingLogId(coachingLogId);
            feedback.setIssueCode(item.getIssueCode());
            feedback.setDescription(item.getDescription());
            feedback.setSetNo(item.getSetNo());
            feedback.setRepNo(item.getRepNo());
            feedback.setTimestamp(item.getTimestamp());

            coachingFeedbackMapper.insertCoachingFeedback(feedback);
        });
    }

    public String getExerciseNameById(int exerciseId) {
        return coachingExerciseMapper.getCoachingExerciseNameById(exerciseId);
    }

    public String getExerciseTitleById(int exerciseId) {
        return coachingExerciseMapper.getCoachingExerciseTitleById(exerciseId);
    }
}
