package com.fitfusion.service;

import com.fitfusion.mapper.AvoidPartsMapper;
import com.fitfusion.mapper.ConditionMapper;
import com.fitfusion.mapper.TargetPartsMapper;
import com.fitfusion.vo.AvoidPart;
import com.fitfusion.vo.Condition;
import com.fitfusion.vo.TargetPart;
import com.fitfusion.web.form.ExerciseConditionForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ExerciseConditionService {

    private final AvoidPartsMapper avoidPartsMapper;
    private final ConditionMapper conditionMapper;
    private final TargetPartsMapper targetPartsMapper;

    public ExerciseConditionForm getConditionAndAvoidAndTargetByUserId(int userId) {
        List<String> avoidParts = avoidPartsMapper.getAvoidPartsByUserId(userId);
        List<String> targetParts = targetPartsMapper.getTargetPartsByUserId(userId);
        String condition = conditionMapper.getConditionLevelByUserId(userId);
        
        ExerciseConditionForm formData = new ExerciseConditionForm();
        formData.setAvoidParts(avoidParts);
        formData.setTargetParts(targetParts);
        formData.setConditionLevel(condition);
        
        return formData;
    }

    @Transactional
    public void saveConditionAndAvoidAndTarget(int userId, String conditionLevel, List<String> avoidParts, List<String> targetParts) {

        List<String> safeAvoid = normalize(avoidParts);
        List<String> safeTarget = normalize(targetParts);

        avoidPartsMapper.deleteAvoidPartsByUserId(userId);
        targetPartsMapper.deleteTargetPartsByUserId(userId);
        conditionMapper.deleteConditionByUserId(userId);

        int conditionId = conditionMapper.getNextConditionId();

        Condition condition = new Condition();
        condition.setConditionId(conditionId);
        condition.setUserId(userId);
        condition.setConditionLevel(conditionLevel);
        conditionMapper.insertCondition(condition);

            for (String bodyName : safeAvoid) {
                AvoidPart avoidPart = new AvoidPart();
                avoidPart.setUserId(condition.getUserId());
                avoidPart.setConditionId(conditionId);
                avoidPart.setBodyName(bodyName);

                avoidPartsMapper.insertAvoidPart(avoidPart);
            }


            for (String bodyName : safeTarget) {
                TargetPart targetPart = new TargetPart();
                targetPart.setUserId(condition.getUserId());
                targetPart.setConditionId(conditionId);
                targetPart.setBodyName(bodyName);

                targetPartsMapper.insertTargetPart(targetPart);
            }
        }

    @Transactional
    public void deleteConditionByUserId(int userId) {
        targetPartsMapper.deleteTargetPartsByUserId(userId);
        avoidPartsMapper.deleteAvoidPartsByUserId(userId);
        conditionMapper.deleteConditionByUserId(userId);
    }

    public List<String> getTargetPartsByUserId(int userId) {
        return targetPartsMapper.getTargetPartsByUserId(userId);
    }

    public List<String> getAvoidPartsByUserId(int userId) {
        return avoidPartsMapper.getAvoidPartsByUserId(userId);
    }

    public int getConditionIdByUserId(int userId) {
        return conditionMapper.getConditionIdByUserId(userId);
    }

    @Transactional
    public void deleteTargetPartsByConditionId(int conditionId) {
        targetPartsMapper.deleteTargetPartsByConditionId(conditionId);
    }

    @Transactional
    public void deleteAvoidPartsByConditionId(int conditionId) {
        avoidPartsMapper.deleteAvoidPartsByConditionId(conditionId);
    }

    public String getConditionLevelByUserId(int userId) {
         return conditionMapper.getConditionLevelByUserId(userId);
    }

    private List<String> normalize(List<String> src) {
        if (src == null || src.isEmpty()) return Collections.emptyList();
        return new ArrayList<>(new LinkedHashSet<>(src));
    }

}
