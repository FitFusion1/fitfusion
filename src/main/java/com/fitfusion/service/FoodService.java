package com.fitfusion.service;

import com.fitfusion.dto.*;
import com.fitfusion.mapper.FoodMapper;
import com.fitfusion.mapper.MealRecordMapper;
import com.fitfusion.util.NutrientSum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodService {

    private final FoodMapper foodMapper;
    private final MealRecordMapper mealRecordMapper;

    // 새로운 UI 도입 후

    // 음식명 또는 제조사명으로 검색
    public List<FoodDto> searchFoods(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return Collections.emptyList();
        }
        return foodMapper.searchFoodsByKeyword(keyword);
    }

    /**
     * 선택한 음식 목록 가져오기
     */
    public List<SelectedFoodDto> getSelectedFoods(List<Integer> foodIds) {
        return foodMapper.findFoodsByIds(foodIds);
    }

    /**
     * 선택 음식들의 영양소 합계 계산
     */
    public NutrientSummaryDto calculateTotals(List<SelectedFoodDto> foods) {
        return NutrientSum.sum(foods);
    }

//    /**
//     * MealRecord 저장 (단건/다건 모두 처리 가능)
//     */
//    public void saveMealRecords(List<MealRecordDto> mealRecords) {
//        if (mealRecords == null || mealRecords.isEmpty()) {
//            throw new IllegalArgumentException("Meal records cannot be empty");
//        }
//        if (mealRecords.size() > 1000) {
//            throw new IllegalArgumentException("Too many meal records. Max allowed: 1000");
//        }
//
//        // createdDate → DB에서 SYSDATE
//        // updatedDate → NULL
//        // recordDate → 사용자가 선택한 날짜가 그대로 넘어옴
//
//        mealRecordMapper.insertMealRecords(mealRecords);
//    }

    // 새로운 UI 도입 전
    /**
     * 전체 음식 조회 (Export용)
     */
    public List<FoodDto> findAllFoods() {
        return foodMapper.findAllForExport();
    }

    /**
     * 음식 상세 조회
     */
    //☆ 단건 상세 조회
    public FoodDto getFoodById(Integer foodId) {
        return foodMapper.findFoodById(foodId);
    }

    /**
     * ☆ 선택된 음식 목록을 FoodDto로 반환
     */
    public List<FoodDto> getFoodsByIds(List<Integer> foodIds) {
        if (foodIds == null || foodIds.isEmpty()) {
            return Collections.emptyList();
        }
        return foodMapper.findFoodsAsFoodDto(foodIds);
    }

    /**
     * ☆ UI 전용: SelectedFoodDto 반환
     */
    public List<FoodDto> findFoodsByIds(List<Integer> foodIds) {
        if (foodIds == null || foodIds.isEmpty()) {
            return Collections.emptyList();
        }
        return foodMapper.findFoodsAsFoodDto(foodIds);
    }

    /**
     * 로직용: 전체 상세 데이터 조회 (FoodDto)
     */
    public List<FoodDto> findFoodsAsFoodDto(List<Integer> foodIds) {
        if (foodIds == null || foodIds.isEmpty()) {
            return Collections.emptyList();
        }
        return foodMapper.findFoodsAsFoodDto(foodIds);
    }

    /**
     * 키워드 검색 (페이징)
     */
    public PageResponseDto<FoodDto> searchFoodsPaged(String keyword, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<FoodDto> list = foodMapper.searchFoodsByPaging(keyword, offset, pageSize);
        long total = foodMapper.countFoodsByKeyword(keyword);

        System.out.println("[DEBUG] 검색어: " + keyword + ", pageNum: " + pageNum + ", pageSize: " + pageSize);
        System.out.println("[DEBUG] total count: " + total + ", 결과 개수: " + list.size());
        return new PageResponseDto<>(list, total, pageNum, pageSize); // totalPages 계산 포함
    }

    // 즐겨찾기 음식 조회
    public List<FoodDto> findFavoriteFoods(int userId) {
        return foodMapper.findFavoriteFoods(userId);
    }

    // 자주 먹는 음식 조회
    public List<FoodDto> findFrequentFoods(int userId) {
        return foodMapper.findFrequentFoods(userId);
    }

//    /**
//     * 카테고리별 검색
//     */
//    public List<FoodDto> searchByCategory(String foodCat1Code) {
//        return foodMapper.findByFoodCat1Code(foodCat1Code);
//    }

//    /**
//     * 수입 여부로 검색
//     */
//    public List<FoodDto> searchByImported(String isImported) {
//        return foodMapper.findByImported(isImported);
//    }

    /**
     * 음식 등록
     */
    @Transactional
    public void addFood(FoodDto foodDto) {
        foodMapper.insertFood(foodDto);
    }

    /**
     * 음식 수정
     */
    @Transactional
    public void updateFood(FoodDto foodDto) {
        foodMapper.updateFood(foodDto);
    }

    /**
     * 음식 삭제
     */
    @Transactional
    public void deleteFood(Integer foodId) {
        foodMapper.deleteFood(foodId);
    }

}
