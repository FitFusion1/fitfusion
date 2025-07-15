package com.fitfusion.service;

import com.fitfusion.dto.FoodDto;
import com.fitfusion.dto.PageResponseDto;
import com.fitfusion.mapper.FoodMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodService {

    private final FoodMapper foodMapper;

    public List<FoodDto> findAllFoods() {
        return foodMapper.findAll();
    }

    public List<FoodDto> searchFoods(String keyword) {
        return foodMapper.searchFoods(keyword);
    }

    public PageResponseDto<FoodDto> searchFoodsWithPaging(String keyword, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;

        List<FoodDto> list = foodMapper.searchFoodsByPaging(keyword, offset, pageSize);
        long total = foodMapper.countFoodsByKeyword(keyword);

        return new PageResponseDto<>(list, total, pageNum, pageSize);
    }

    public List<FoodDto> searchByCategory(String categoryCode) {
        return foodMapper.findByCategory(categoryCode);
    }

    public List<FoodDto> searchByImported(String isImported) {
        return foodMapper.findByImported(isImported);
    }

    public void addFood(FoodDto foodDto) {
        foodMapper.insertFood(foodDto);
    }

    public void updateFood(FoodDto foodDto) {
        foodMapper.updateFood(foodDto);
    }

    public void deleteFood(String foodId) {
        foodMapper.deleteFood(foodId);
    }

    /**
     * DB에 저장된 모든 음식명을 반환
     */
    public List<String> findAllFoodNames() {
        return foodMapper.findAllFoodNames();
    }

}
