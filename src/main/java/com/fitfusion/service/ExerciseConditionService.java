package com.fitfusion.service;

import com.fitfusion.mapper.AvoidPartsMapper;
import com.fitfusion.mapper.ConditionMapper;
import com.fitfusion.mapper.TargetPartsMapper;
import com.fitfusion.vo.AvoidPart;
import com.fitfusion.vo.Condition;
import com.fitfusion.vo.TargetPart;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExerciseConditionService {

    private final int userId = 1;
    private final AvoidPartsMapper avoidPartsMapper;
    private final ConditionMapper conditionMapper;
    private final TargetPartsMapper targetPartsMapper;

    @Transactional
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
}
