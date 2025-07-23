package com.fitfusion.batch;

import com.fitfusion.dto.FoodDto;
import com.fitfusion.dto.FoodSaveResult;
import com.fitfusion.service.FoodApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FoodDataImporter {

    private final FoodApiService foodApiService;

    /**
     * 공공데이터 식품 영양 DB 수집 (키워드별 수집, 저장 성공 통계 포함)
     *
     * 나중에 관리자 페이지? DB 저장 기능이 필요한 페이지 만들면 AdminController 이런걸로 호출할 수 있을지도...?
     *
     * <아직 없는 기능 - 전부 구현이 어렵진 않은데 후순위, 아래의 기능은 그냥 확장 가능성 예시로 작성>
     * - 저장 시작 후 중지 기능
     *
     * - 실행 중간에 저장 종료 시  📊 [{}] 키워드 요약             출력
     *                         ─────────────────────────────
     *                          - 총 응답 항목 수:   {}
     *                          - 저장 성공:        {}
     *                          - 중복 제외:        {}
     *                          - 저장 실패:        {}
     *                          - 요청 횟수:        {}
     *
     * - 음식 이름 외에 다른 방법으로 DB조회 후 저장 ex) 제조사, 식품군, 영양소
     *
     * - 저장 실행 시작 시 받을 DB 전체 몇 건 있는지 알려주기
     *
     * - 저장 진행률
     *
     * - 오늘의 총 요청 횟수 카운트 ('공공 API 일일 요청 제한: 10,000회'가 존재함)
     */
    // =================== 설정 =================== //
    // 수집할 키워드 목록 (필요에 따라 변경) (우선 수집 대상)
    private static final List<String> KEYWORDS = List.of("닭가슴살", "밥", "곤약", "라면");

    // 하루 트래픽 제한 ((공공 API 정책) API 일일 트래픽 제한: 10,000 요청/일)
    private static final int MAX_API_REQUESTS_PER_DAY = 10_000;

    // 하루 최대 저장할 건수 제한 (데이터 품질 및 운영 효율을 위해 설정)
    private static final int MAX_RECORDS_PER_RUN = 5000;

    // 한 페이지당 API에서 가져올 항목 수 (공공데이터 API 최대 허용 값: 100)
    private static final int PAGE_SIZE = 100;

    // 키워드별 최대 조회 페이지 수
    // 예: MAX_PAGES_PER_KEYWORD(1000) × 1 API 요청(반복문의 1회 searchFood() 호출)) = 최대 1000회 요청
    // → 여러 키워드 수집 시 API 일일 요청 한도(10,000회) 초과에 주의
    private static final int MAX_PAGES_PER_KEYWORD = 1000;

    // 각 페이지 요청 간 딜레이(ms)
    // → API 서버 부하 방지 및 차단 방지 목적
    private static final int DELAY_MILLIS = 200;

    /**
     * ✅ 키워드 기반 식품 데이터 수집 (관리자 API에서 호출)
     */
    public String importDataByKeyword(String keyword) {
        log.info("✅ [{}] 데이터 수집 시작...", keyword);
        int totalSaved = 0, totalRequests = 0, saved = 0, duplicated = 0, failed = 0, totalReceived = 0;

        try {
            for (int page = 1; page <= MAX_PAGES_PER_KEYWORD; page++) {
                if (totalSaved >= MAX_RECORDS_PER_RUN || totalRequests >= MAX_API_REQUESTS_PER_DAY) {
                    log.warn("⛔ 한도 도달 → 저장: {}, 요청: {}", totalSaved, totalRequests);
                    break;
                }

                List<FoodDto> list = foodApiService.searchFood(keyword, page, PAGE_SIZE);
                totalRequests++;

                if (list.isEmpty()) {
                    if (page == 1) {
                        log.info("📭 API 서버에 '{}' 키워드 데이터 없음", keyword);
                    } else {
                        log.info("📭 '{}' 키워드 → {}페이지에 더 이상 데이터 없음 (수집 종료)", keyword, page);
                    }
                    break;
                }

                totalReceived += list.size();
                FoodSaveResult result = foodApiService.saveFoods(list);
                saved += result.getSaved();
                duplicated += result.getDuplicated();
                failed += result.getFailed();
                totalSaved += result.getSaved();

                Thread.sleep(DELAY_MILLIS);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "❌ 작업 중단 (Interrupted)";
        }

        String summary = String.format("""
                ✅ [%s] 수집 완료
                ─────────────────────────────
                - 총 응답 항목 수:   %,d
                - 저장 성공:       %,d
                - 중복 제외:       %,d
                - 저장 실패:       %,d
                - 요청 횟수:       %,d
                """, keyword, totalReceived, saved, duplicated, failed, totalRequests);

        log.info(summary);
        return summary;
    }
}