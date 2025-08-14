package com.fitfusion.service;

import com.fitfusion.dto.MealRecordDto;
import com.fitfusion.dto.NutrientSummaryDto;
import com.fitfusion.dto.SelectedFoodDto;
import com.fitfusion.enums.MealTime;
import com.fitfusion.mapper.FoodMapper;
import com.fitfusion.mapper.MealRecordMapper;
import com.fitfusion.util.NutrientSum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MealRecordService {

    private final MealRecordMapper mealRecordMapper;
    private final FoodMapper foodMapper;

    /**
     *  특정 날짜 식단 기록을 끼니별로 그룹화 (Map)
     *
     * 사용 상황:
     * - 메인 페이지 렌더링 시 아침/점심/저녁/간식 구분 표시
     * @param userId    사용자 ID
     * @param recordDate yyyy-MM-dd
     * @return Map<String, List<MealRecordDto>> (키: 끼니, 값: 해당 끼니 기록)
     */
    public Map<MealTime, List<MealRecordDto>> getMealRecordsGroupedByMealTime(int userId, Date recordDate) {
        // 1. DB에서 해당 날짜의 식단 기록 전체 조회
        List<MealRecordDto> records = mealRecordMapper.getMealRecordsByDate(userId, recordDate);

        // 2. Java Stream을 사용해 mealTime별 그룹화 (BREAKFAST, LUNCH, DINNER, SNACK)
        return records.stream()
                .collect(Collectors.groupingBy(MealRecordDto::getMealTime));
    }

    /**
     *  특정 날짜 식단 기록 리스트 그대로 반환
     *  사용 상황 : Meal/Record 페이지 조회
     */
    public List<MealRecordDto> getMealRecordsByDate(int userId, Date recordDate) {
        return mealRecordMapper.getMealRecordsByDate(userId, recordDate);
    }

    /**
     *  단건 식단 기록 추가
     *
     * 사용 상황:
     * - AJAX 방식 (음식 하나씩 추가)
     * - 테스트용
     */
    public void addMealRecord(MealRecordDto dto) {
        mealRecordMapper.insertMealRecord(dto);
    }

    /**
     *  다건 식단 기록 저장
     *
     * 사용 상황:
     * - FatSecret UX (모달에서 여러 항목 선택 후 "저장" 버튼 클릭)
     * - 성능 향상: 여러 insert를 batch로 처리
     */
    @Transactional
    public void saveMealRecords(List<MealRecordDto> mealRecords) {
        if (mealRecords != null && !mealRecords.isEmpty()) {
            mealRecordMapper.insertMealRecords(mealRecords);
        }
    }

@Transactional
public void deleteMealRecord(int mealRecordId, int userId) {
    MealRecordDto record = mealRecordMapper.findByMealRecordId(mealRecordId);
    if (record == null) {
        throw new IllegalArgumentException("삭제할 식단 기록이 없습니다.");
    }

    if (record.getUserId() != userId) {
        throw new AccessDeniedException("삭제 권한이 없습니다.");
    }

    int deletedCount = mealRecordMapper.deleteByMealRecordId(mealRecordId);
    if (deletedCount == 0) {
        throw new IllegalStateException("삭제 실패: 해당 식단 기록을 찾을 수 없습니다.");
    }
}

/**
 *  선택된 음식 상세 조회 (detail 뷰에서 사용)
 *
 *  사용 상황:
 *  - 선택된 음식들 영양소 확인
 */
    public MealRecordDto getMealRecordDetail(int mealRecordId) {
        return mealRecordMapper.selectMealRecordDetailById(mealRecordId);
    }

//    /**
//     *  선택된 음식 상세 조회 (검색 → 선택 후 표시)
//     *
//     * 사용 상황:
//     * - 선택된 음식들 영양소 확인
//     */
//    public List<SelectedFoodDto> getSelectedFoods(List<Integer> foodIds) {
//        return foodMapper.findFoodsByIds(foodIds);
//    }

    /**
     *  선택된 음식들의 영양소 합계 계산
     *
     * 사용 상황:
     * - UI에서 선택한 음식 합산 칼로리 표시
     */
    public NutrientSummaryDto calculateTotals(List<SelectedFoodDto> foods) {
        return NutrientSum.sum(foods);
    }

    /**
     * 오늘 섭취 영양소 합계 (Controller에서 nutrientSummary로 사용)
     *
     * 사용 상황:
     * - 메인 페이지 하단: 칼로리/탄단지 합계 표시
     */
    public Map<String, Object> calculateSummary(int userId, Date recordDate) {
        return mealRecordMapper.getTodayNutrientSummary(userId, recordDate);
    }

    /**
     * 섭취량 업데이트
     * @param mealRecordId 식단 기록 ID
     * @param userIntake  업데이트할 섭취량
     */
    @Transactional
    public void updateIntake(int mealRecordId, double userIntake) {
        log.info("updateIntake 호출됨: mealRecordId={}, userIntake={}", mealRecordId, userIntake);
        mealRecordMapper.updateIntake(mealRecordId, userIntake);
    }
}
