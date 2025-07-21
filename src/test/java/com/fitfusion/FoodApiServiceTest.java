package com.fitfusion;

import com.fitfusion.dto.FoodDto;
import com.fitfusion.service.FoodApiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

// 받는 조건 좀 다듬으면 관리자 페이지에 쓸 수 있을듯(중간부터 다시 받을 수 있는 방법, 조건을 입력할 입력란 구현)
@SpringBootTest
class FoodApiServiceTest {

    @Autowired
    private FoodApiService foodApiService;

    // ✅ 하루 트래픽 한계 고려해서 제한 건수 설정 (10000건 이하 추천)
    private static final int MAX_RECORDS_PER_RUN = 5000;

    @Test
    void testSafeFoodImport() {
        List<String> keywords = List.of("바나나", "방울토마토", "샐러드");
        //List<String> keywords = List.of("밥", "라면", "닭가슴살");

        int insertedCount = 0;

        outer:
        for (String keyword : keywords) {
            for (int page = 1; page <= 1000; page++) {
                List<FoodDto> list = foodApiService.searchFood(keyword, page, 100);
                if (list.isEmpty()) break;

                foodApiService.saveFoods(list);
                insertedCount += list.size();

                // ✅ 일일 트래픽 초과 방지
                if (insertedCount >= MAX_RECORDS_PER_RUN) {
                    System.out.println("⛔ 최대 수집 건수 도달 (" + insertedCount + "건) → 중단");
                    break outer;
                }

                try {
                    Thread.sleep(200); // API 서버 과부하 방지
                } catch (InterruptedException ignored) {}
            }
        }

        System.out.println("✅ 테스트 종료. 총 저장 시도 건수: " + insertedCount + "건");
    }
}

// 개선 전 버전1. 5500건까지 자동으로 받아봄. 트래픽 제한 걸릴까봐 중간에 취소함.
//package com.fitfusion;
//
//import com.fitfusion.service.FoodApiService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.List;
//
//@SpringBootTest
//class FoodApiServiceTest {
//
//    @Autowired
//    private FoodApiService foodApiService;
//
//    @Test
//    void testFoodImport() {
//        List<String> keywords = List.of("밥", "라면", "닭가슴살");
//        foodApiService.importPrioritizedThenRemaining(keywords);
//    }
//}
