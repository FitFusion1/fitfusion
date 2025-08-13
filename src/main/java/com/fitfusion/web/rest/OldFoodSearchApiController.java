//package com.fitfusion.web.rest;
//
//import com.fitfusion.dto.FoodDto;
//import com.fitfusion.dto.PageResponseDto;
//import com.fitfusion.service.FoodService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Slf4j
//@RestController
//@RequestMapping("/api/foods")
//@RequiredArgsConstructor
//
// **
// * 음식 검색 및 상세 조회 API 컨트롤러
// *  - **공공데이터 API가 아닌 DB에서 데이터를 가져옴**
// *
// * 주요 기능:
// *  - DB에 저장된 음식 데이터를 검색 (키워드 + 페이징)
// *  - DB에서 특정 음식의 상세 정보를 조회
// *
// * 특징:
// *  - JSON 응답을 제공하는 REST API
// *  - 사용자 및 관리자 공용 (검색, 상세 보기 기능)
// *  - AJAX 기반 검색 UI에서 호출됨
// */
//public class OldFoodSearchApiController {
//
//    private final FoodService foodService;
//
//    /**
//     * 음식 검색 (키워드 + 페이징)
//     */
//    @GetMapping("/search")
//    public Map<String, Object> searchFoods(
//            @RequestParam String keyword,
//            @RequestParam(defaultValue = "1") int page,
//            @RequestParam(defaultValue = "10") int pageSize
//    ) {
//        PageResponseDto<FoodDto> pageResult = foodService.searchFoodsPaged(keyword, page, pageSize);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("list", pageResult.getList());
//        response.put("total", pageResult.getTotal());
//        response.put("page", pageResult.getPageNum());
//        response.put("pageSize", pageResult.getPageSize());
//        return response;
//    }
//
//    /**
//     * 음식 상세 조회
//     */
//    @GetMapping("/{id}")
//    public FoodDto getFoodById(@PathVariable("id") Integer id) {
//        return foodService.findFoodById(id);
//    }
//}
