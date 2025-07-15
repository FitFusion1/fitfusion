package com.fitfusion.web.controller;

import com.fitfusion.dto.FoodDto;
import com.fitfusion.dto.PageResponseDto;
import com.fitfusion.service.FoodApiService;
import com.fitfusion.service.FoodService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/food")
@RequiredArgsConstructor
public class FoodController {

    private final FoodService foodService;
    private final FoodApiService foodApiService;

    /**
     * DB에서 keyword 검색
     */
    @GetMapping("/search")
    public PageResponseDto<FoodDto> searchFoods(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return foodService.searchFoodsWithPaging(keyword, pageNum, pageSize);
    }

    /**
     * DB에 수동으로 개별 저장
     */
    @PostMapping("/save")
    public ResponseEntity<Void> addFood(@Validated @RequestBody FoodDto foodDto) {
        foodService.addFood(foodDto);
        return ResponseEntity.ok().build();
    }

    /**
     * 공공데이터 API에서 음식정보 import
     * - keyword가 없으면 전체 import
     * - keyword가 있으면 keyword 기준 import
     */
    @PostMapping("/importFromApi")
    public ResponseEntity<String> importFoodsFromApi(
            @RequestParam(required = false) String keyword
    ) {
        int savedCount;
        if (keyword == null || keyword.isBlank()) {
            savedCount = foodApiService.importAllFoods();
        } else {
            savedCount = foodApiService.importFoodsAndSave(keyword);
        }

        String message = String.format(
                "%d개의 데이터를 저장했습니다. (검색어: %s)",
                savedCount,
                (keyword == null || keyword.isBlank()) ? "전체" : keyword
        );

        return ResponseEntity.ok(message);
    }

    /**
     * API 기준 전체 totalCount 조회
     */
    @GetMapping("/totalCount")
    public ResponseEntity<Integer> getTotalCount() {
        int total = foodApiService.getTotalCount(null);
        log.info("전체 totalCount = {}", total);
        return ResponseEntity.ok(total);
    }

    /**
     * 전체 데이터 import (강제 실행)
     */
    @PostMapping("/importAllFromApi")
    public ResponseEntity<String> importAllFoods() {
        int savedCount = foodApiService.importAllFoods();
        return ResponseEntity.ok(savedCount + "개의 데이터를 저장했습니다.");
    }



}
