package com.fitfusion.mapper;

import com.fitfusion.vo.Condition;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ConditionMapper {
    void insertCondition(Condition condition);

    int getNextConditionId();

    void deleteConditionByUserId(int userId);

    String getConditionLevelByUserId(int userId);

    int getConditionIdByUserId(int userId);

}
