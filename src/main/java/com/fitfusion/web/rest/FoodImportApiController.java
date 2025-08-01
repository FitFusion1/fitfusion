package com.fitfusion.web.rest;

import com.fitfusion.dto.FoodDto;
import com.fitfusion.service.FoodApiService;
import com.fitfusion.service.FoodService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin/import/foods")
@RequiredArgsConstructor

/**
 * 관리자용 음식 데이터 관리 API 컨트롤러
 *   ** 공공데이터 API를 통해 음식 정보를 DB에 저장 **
 *
 * 주요 기능:
 *  - 관리자가 수동으로 음식 정보를 추가
 *  - 공공데이터 API 기준 totalCount 조회
 */
public class FoodImportApiController {

    private final FoodService foodService;
    private final FoodApiService foodApiService;

    /**
     * DB에 수동 저장
     *  POST /api/admin/foods
     */
    @PostMapping("/save")
    public ResponseEntity<Void> addFood(@Validated @RequestBody FoodDto foodDto) {
        foodService.addFood(foodDto);
        return ResponseEntity.ok().build();
    }

    /**
     * API에서 음식정보 import (keyword 필수)
     *  POST /api/admin/foods/import
     */
    @PostMapping("/importFromApi")
    public ResponseEntity<String> importFoodsFromApi(@RequestBody Map<String, String> request) {
        String keyword = request.get("keyword");
        if (keyword == null || keyword.isBlank()) {
            return ResponseEntity.badRequest().body("❌ 전체 데이터 import는 지원되지 않습니다. (키워드 필요)");
        }
        String summary = foodApiService.importByKeywords(List.of(keyword));
        return ResponseEntity.ok(summary);
    }

    /**
     * API 기준 totalCount 조회
     *  GET /api/admin/foods/import/count
     */
    @GetMapping("/totalCount")
    public ResponseEntity<Integer> getTotalCount(@RequestParam(required = false) String keyword) {
        int total = foodApiService.getTotalCount(keyword);
        log.info("totalCount (keyword: {}) = {}", keyword, total);
        return ResponseEntity.ok(total);
    }
}
