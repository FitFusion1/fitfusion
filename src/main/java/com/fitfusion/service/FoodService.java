package com.fitfusion.service;

import com.fitfusion.dto.FoodDto;
import com.fitfusion.dto.PageResponseDto;
import com.fitfusion.mapper.FoodMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodService {

    private final FoodMapper foodMapper;

    /**
     * 전체 음식 조회 (Export용)
     */
    public List<FoodDto> findAllFoods() {
        return foodMapper.findAllForExport();
    }

    /**
     * 음식 상세 조회
     */
    public FoodDto findFoodById(Integer foodId) {
        return foodMapper.findFoodByIdWithUnit(foodId);
    }

    /**
     * 키워드 검색 (페이징 없음)
     */
    public List<FoodDto> searchFoods(String keyword) {
        return foodMapper.searchFoodsByKeyword(keyword);
    }

    /**
     * 키워드 검색 (페이징)
     */
    public PageResponseDto<FoodDto> searchFoodsPaged(String keyword, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<FoodDto> list = foodMapper.searchFoodsByPaging(keyword, offset, pageSize);
        long total = foodMapper.countFoodsByKeyword(keyword);
        return new PageResponseDto<>(list, total, pageNum, pageSize);
    }

    /**
     * 카테고리별 검색
     */
    public List<FoodDto> searchByCategory(String foodCat1Code) {
        return foodMapper.findByFoodCat1Code(foodCat1Code);
    }

    /**
     * 수입 여부로 검색
     */
    public List<FoodDto> searchByImported(String isImported) {
        return foodMapper.findByImported(isImported);
    }

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

    /**
     * DB에 저장된 모든 음식명 반환
     */
    public List<String> findAllFoodNames() {
        return foodMapper.findAllFoodNames();
    }
}
