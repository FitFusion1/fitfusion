package com.fitfusion.mapper;

import com.fitfusion.dto.MealRecordDto;
import com.fitfusion.enums.MealTime;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface MealRecordMapper {

    // mealRecordId로 식단 기록 조회
    MealRecordDto findByMealRecordId(@Param("mealRecordId") int mealRecordId);

    /**
     * 특정 음식의 영양소 detail 뷰
     * mealRecordId로 특정 식사 기록 상세 조회
     * @param mealRecordId 조회할 mealRecord ID
     * @return MealRecordDto
     */
    MealRecordDto selectMealRecordDetailById(@Param("mealRecordId") int mealRecordId);

    /**
     * ✅ 특정 날짜의 식단 기록(해당 날짜에 기록된 여러 음식) 조회
     * @param userId 사용자 ID
     * @param recordDate 조회할 날짜 (YYYY-MM-DD)
     */
    List<MealRecordDto> getMealRecordsByDate(@Param("userId") int userId,
                                             @Param("recordDate") Date recordDate);

    List<MealRecordDto> selectMealRecordsByMealTime(@Param("userId") int userId,
                                                    @Param("recordDate") Date recordDate,
                                                    @Param("mealTime") MealTime mealTime);
    /**
     * ✅ 섭취량 업데이트 (USER_INTAKE)
     * @param mealRecordId 식단 기록 ID
     * @param userIntake 변경할 섭취량 (g, mL)
     */
    int updateIntake(@Param("mealRecordId") int mealRecordId, @Param("userIntake") double userIntake);

    /**
     * ✅ 식단 기록 삭제
     * @param mealRecordId 삭제할 식단의 기록 ID
     */
    int deleteByMealRecordId(@Param("mealRecordId") int mealRecordId);

    /**
     * ✅ 특정 날짜 영양소 합계 조회
     * @param userId 사용자 ID
     * @param recordDate 조회할 날짜 (YYYY-MM-DD)
     * @return 칼로리, 탄수화물, 단백질, 지방 등 합계 Map
     */
    Map<String, Object> getTodayNutrientSummary(@Param("userId") int userId,
                                                @Param("recordDate") Date recordDate);

    /**
     * ✅ 단건 식단 기록 저장
     */
    void insertMealRecord(MealRecordDto dto);

    /**
     * 다건 식단 기록 저장
     */
    void insertMealRecords(@Param("mealRecords") List<MealRecordDto> mealRecords);
}
