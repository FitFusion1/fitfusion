//package com.fitfusion;
//
//import com.fitfusion.dto.FoodDto;
//import com.fitfusion.dto.FoodSaveResult;
//import com.fitfusion.service.FoodApiService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.List;
//
//
//@SpringBootTest
//class FoodApiServiceTest {
//
//    @Autowired
//    private FoodApiService foodApiService;
//
//    /*
//     * 실행 시 해당 키워드의 음식 DB 저장이 진행됩니다.(API → DB)
//     *  실행 조건: 해당 api 키 존재,
//     *            DTO에 알맞는 FITFUSION_FOOD_ITEMS 테이블 존재,
//     *            FoodDataImporter클래스가 주석 처리 상태일 때(이 테스트 통과하고 만든 게 FoodDataImporter임.)
//     */
//
//    /**
//     * 공공데이터 식품 영양 DB 수집 (키워드별 수집, 저장 성공 통계 포함)
//     */
//    // =================== 설정 =================== //
//    // 수집할 키워드 목록 (필요에 따라 변경) (우선 수집 대상)
//    private static final List<String> KEYWORDS = List.of("닭가슴살", "밥", "곤약", "라면");
//
//    // 하루 트래픽 제한 ((공공 API 정책) API 일일 트래픽 제한: 10,000 요청/일)
//    private static final int MAX_API_REQUESTS_PER_DAY = 10_000;
//
//    // 하루 최대 저장할 건수 제한 (데이터 품질 및 운영 효율을 위해 설정)
//    private static final int MAX_RECORDS_PER_RUN = 5000;
//
//    // 한 페이지당 API에서 가져올 항목 수 (공공데이터 API 최대 허용 값: 100)
//    private static final int PAGE_SIZE = 100;
//
//    // 키워드별 최대 조회 페이지 수
//    // 예: MAX_PAGES_PER_KEYWORD(1000) × 1 API 요청(반복문의 1회 searchFood() 호출)) = 최대 1000회 요청
//    // → 여러 키워드 수집 시 API 일일 요청 한도(10,000회) 초과에 주의
//    private static final int MAX_PAGES_PER_KEYWORD = 1000;
//
//    // 각 페이지 요청 간 딜레이(ms)
//    // → API 서버 부하 방지 및 차단 방지 목적
//    private static final int DELAY_MILLIS = 200;
//
//    // =================== 테스트 =================== //
//    @Test
//    void testImportFoodsWithSaveResult() {
//        int totalSaved = 0;
//        int totalRequests = 0;
//
//        for (String keyword : KEYWORDS) {
//            int saved = 0;
//            int duplicated = 0;
//            int failed = 0;
//            int requests = 0;
//            int totalReceived = 0;
//
//            for (int page = 1; page <= MAX_PAGES_PER_KEYWORD; page++) {
//                if (totalSaved >= MAX_RECORDS_PER_RUN || totalRequests >= MAX_API_REQUESTS_PER_DAY) {
//                    System.out.printf("⛔ 한도 도달 → 저장: %,d건 | 요청: %,d회%n", totalSaved, totalRequests);
//                    break;
//                }
//
//                List<FoodDto> list = foodApiService.searchFood(keyword, page, PAGE_SIZE);
//                totalRequests++;
//                requests++;
//
//                if (list.isEmpty()) {
//                    if (page == 1) {
//                        System.out.printf("📭 API 서버에 '%s' 키워드 데이터 없음%n", keyword);
//                    } else {
//                        System.out.printf("📭 '%s' 키워드 → %d페이지에 더 이상 데이터 없음 (수집 종료)%n", keyword, page);
//                    }
//                    break;
//                }
//
//                totalReceived += list.size();
//
//                FoodSaveResult result = foodApiService.saveFoods(list);
//                saved += result.getSaved();
//                duplicated += result.getDuplicated();
//                failed += result.getFailed();
//                totalSaved += result.getSaved();
//
//                try {
//                    Thread.sleep(DELAY_MILLIS);
//                } catch (InterruptedException ignored) {}
//            }
//
//            System.out.printf("""
//
//                    📊 [%s] 키워드 요약
//                    ─────────────────────────────────────
//                    - 총 응답 항목 수: %,d건
//                    - 저장 성공:       %,d건
//                    - 중복 제외:       %,d건
//                    - 저장 실패:       %,d건
//                    - 요청 횟수:       %,d회
//                    """, keyword, totalReceived, saved, duplicated, failed, requests);
//        }
//
//        System.out.printf("\n✅ 전체 완료 → 총 저장: %,d건 | 총 요청: %,d회%n", totalSaved, totalRequests);
//    }
//}
//
