package com.fitfusion.web.rest;

import com.fitfusion.dto.FoodDto;
import com.fitfusion.mapper.FoodMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/foods")
public class FoodSearchApiController {

    private final FoodMapper foodMapper;

    @GetMapping("/search")
    public List<FoodDto> searchFoods(@RequestParam("keyword") String keyword) {
        return foodMapper.searchFoodsByKeyword("%" + keyword + "%");
    }

    @GetMapping("/{id}")
    public FoodDto getFoodById(@PathVariable("id") Integer id) {
        return foodMapper.findById(id);
    }

    @GetMapping("/search/paged")
    public List<FoodDto> searchFoodsPaged(
            @RequestParam("keyword") String keyword,
            @RequestParam("page") int page,
            @RequestParam("pageSize") int pageSize) {
        int offset = (page - 1) * pageSize;
        return foodMapper.searchFoodsByPaging(keyword, offset, pageSize);
    }
}
