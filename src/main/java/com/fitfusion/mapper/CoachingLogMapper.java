package com.fitfusion.mapper;

import com.fitfusion.dto.CoachingHistoryDto;
import com.fitfusion.vo.CoachingLog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CoachingLogMapper {

    void insertCoachingLog(CoachingLog coachingLog);
    List<CoachingHistoryDto> getCoachingHistoryByUserId(int userId);
    CoachingHistoryDto getCoachingHistoryById(int id);
}
