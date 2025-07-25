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

import java.util.List;
import java.util.Map;

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
    
//    바로 아래 @PostMapping("/importFromApi") 멀쩡하면 지워도 됨((위)@RequestParam → (아래)@RequestBody로 받는다는 입력 방식)
//    /**
//     * 공공데이터 API에서 음식정보 import
//     * - keyword가 없으면 전체 import
//     * - keyword가 있으면 keyword 기준 import
//     */
//    @PostMapping("/importFromApi")
//    public ResponseEntity<String> importFoodsFromApi(
//            @RequestParam(required = false) String keyword
//    ) {
//        int savedCount;
//        if (keyword == null || keyword.isBlank()) {
//            savedCount = foodApiService.importAllFoods();
//        } else {
//            savedCount = foodApiService.importFoodsAndSave(keyword);
//        }
//
//        String message = String.format(
//                "%d개의 데이터를 저장했습니다. (검색어: %s)",
//                savedCount,
//                (keyword == null || keyword.isBlank()) ? "전체" : keyword
//        );
//
//        return ResponseEntity.ok(message);
//    }

    /**
     * 공공데이터 API에서 음식정보 import
     * - 요청 바디의 keyword가 없으면 전체 import는 미구현 (예외 처리)
     * - keyword가 있으면 해당 키워드 기준 import
     */
    @PostMapping("/importFromApi")
    public ResponseEntity<String> importFoodsFromApi(@RequestBody Map<String, String> request) {
        String keyword = request.get("keyword");

        if (keyword == null || keyword.isBlank()) {
            // 전체 import 기능은 현재 서비스에 없음 → 안내 메시지 반환
            return ResponseEntity.badRequest().body("❌ 전체 데이터 import 기능은 지원되지 않습니다. (키워드 필요)");
        }

        // importByKeywords()는 리스트를 받음 → keyword를 리스트로 변환
        String summary = foodApiService.importByKeywords(List.of(keyword));

        return ResponseEntity.ok(summary);
    }

//    바로 아래 @GetMapping("/totalCount") 멀쩡하면 지워도 됨
//    /**
//     * API 기준 전체 totalCount 조회
//     */
//    @GetMapping("/totalCount")
//    public ResponseEntity<Integer> getTotalCount() {
//        int total = foodApiService.getTotalCount(null);
//        log.info("전체 totalCount = {}", total);
//        return ResponseEntity.ok(total);
//    }

     /**
     * API 기준 전체 totalCount 조회
     */
    @GetMapping("/totalCount")
    public ResponseEntity<Integer> getTotalCount(@RequestParam(required = false) String keyword) {
        int total = foodApiService.getTotalCount(keyword);
        log.info("totalCount (keyword: {}) = {}", keyword, total);
        return ResponseEntity.ok(total);
    }


// /**
// * @deprecated 이제는 /importFromApi 로 keyword 없이 요청하세요.
//    @PostMapping("/importFromApi")에서 키워드가 없으면 전체 데이터를 import하므로 중복되는 기능
// */
//    /**
//     * 전체 데이터 import (강제 실행)
//     */
//    @PostMapping("/importAllFromApi")
//    public ResponseEntity<String> importAllFoods() {
//        int savedCount = foodApiService.importAllFoods();
//        return ResponseEntity.ok(savedCount + "개의 데이터를 저장했습니다.");
//    }
}
