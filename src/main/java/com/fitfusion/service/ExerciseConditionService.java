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

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ExerciseConditionService {

    private final int userId = 1;
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
    
    public int saveConditionAndAvoidAndTartget(int userId, String conditionLevel, List<String> avoidParts, List<String> targetParts) {
        avoidPartsMapper.deleteAvoidPartsByUserId(userId);
        targetPartsMapper.deleteTargetPartsByUserId(userId);
        conditionMapper.deleteConditionByUserId(userId);

        int conditionId = conditionMapper.getNextConditionId();

        Condition condition = new Condition();
        condition.setConditionId(conditionId);
        condition.setUserId(userId);
        condition.setConditionLevel(conditionLevel);

        conditionMapper.insertCondition(condition);

        if (avoidParts != null && !avoidParts.isEmpty()) {
            for (String bodyName : avoidParts) {
                AvoidPart avoidPart = new AvoidPart();
                avoidPart.setUserId(condition.getUserId());
                avoidPart.setConditionId(conditionId);
                avoidPart.setBodyName(bodyName);

                avoidPartsMapper.insertAvoidPart(avoidPart);
            }
        }

        if (targetParts != null && !targetParts.isEmpty()) {
            for (String bodyName : targetParts) {
                TargetPart targetPart = new TargetPart();
                targetPart.setUserId(condition.getUserId());
                targetPart.setConditionId(conditionId);
                targetPart.setBodyName(bodyName);

                targetPartsMapper.insertTargetPart(targetPart);
            }
        }
        return conditionId;
    }

    public void saveAvoidParts(int userId, int conditionId, List<String> avoidParts) {
        avoidPartsMapper.deleteAvoidPartsByUserId(userId);
        if (avoidParts != null) {
            for (String part : avoidParts) {
                AvoidPart avoidPart = new AvoidPart();
                avoidPart.setUserId(userId);
                avoidPart.setConditionId(conditionId);
                avoidPart.setBodyName(part);

                avoidPartsMapper.insertAvoidPart(avoidPart);
            }
        }
    }

    public int saveCondition(int userId, String conditionLevel) {
        int conditionId = conditionMapper.getNextConditionId();

        Condition condition = new Condition();
        condition.setConditionId(conditionId);
        condition.setUserId(userId);
        condition.setConditionLevel(conditionLevel);

        conditionMapper.insertCondition(condition);

        return conditionId;
    }

    public void deleteConditionByUserId(int userId) {
        conditionMapper.deleteTargetPartsByUserId(userId);
        conditionMapper.deleteAvoidPartsByUserId(userId);
        conditionMapper.deleteConditionByUserId(userId);
    }

    public int getConditionIdByUserId(int userId) {
        return conditionMapper.getConditionIdByUserId(userId);
    }

    public void deleteTargetPartsByConditionId(int conditionId) {
        targetPartsMapper.deleteTargetPartsByConditionId(conditionId);
    }

    public void deleteAvoidPartsByConditionId(int conditionId) {
        avoidPartsMapper.deleteAvoidPartsByConditionId(conditionId);
    }

    public String getConditionLevelByUserId(int userId) {
         return conditionMapper.getConditionLevelByUserId(userId);
    }

}
