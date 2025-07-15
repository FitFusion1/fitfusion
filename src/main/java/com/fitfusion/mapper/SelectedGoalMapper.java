package com.fitfusion.mapper;

import com.fitfusion.vo.SelectedGoal;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SelectedGoalMapper {

    SelectedGoal findByUserId(int userId);

    void insertSelectedGoal(SelectedGoal selectedGoal);

    void updateSelectedGoal(SelectedGoal selectedGoal);

    void deleteByUserId(int userId);
}
