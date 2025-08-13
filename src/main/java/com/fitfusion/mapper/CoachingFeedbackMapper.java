package com.fitfusion.mapper;

import com.fitfusion.vo.CoachingFeedback;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CoachingFeedbackMapper {

    void insertCoachingFeedback(CoachingFeedback coachingFeedback);
    List<CoachingFeedback> selectFeedbackByCoachingLogId(int coachingLogId);
    
}
