package com.fitfusion.service;

import com.fitfusion.mapper.SelectedGoalMapper;
import com.fitfusion.vo.SelectedGoal;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SelectedGoalService {

    @Autowired
    private SelectedGoalMapper selectMapper;

    public void selectGoal(int userId, int goalId) {
        SelectedGoal existing = selectMapper.findByUserId(userId);
        SelectedGoal newSelection = new SelectedGoal();
        newSelection.setUserId(userId);
        newSelection.setGoalId(goalId);

        if (existing == null) {
            selectMapper.insertSelectedGoal(newSelection);
        } else {
            selectMapper.updateSelectedGoal(newSelection);
        }
    }

    public SelectedGoal getSelectedGoal(int userId) {
        return selectMapper.findByUserId(userId);
    }


    public void deleteSelectedGoal(int userId) {
        selectMapper.deleteByUserId(userId);
    }

    public void updateSelectedGoal(int userId, int goalId) {
        SelectedGoal selectedGoal = new SelectedGoal();
        selectedGoal.setUserId(userId);
        selectedGoal.setGoalId(goalId);
        selectMapper.updateSelectedGoal(selectedGoal);
    }
}
