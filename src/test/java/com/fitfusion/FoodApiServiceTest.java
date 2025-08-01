package com.fitfusion;

import com.fitfusion.config.RestTemplateConfig;
import com.fitfusion.dto.FoodDto;
import com.fitfusion.dto.FoodSaveResult;
import com.fitfusion.service.FoodApiService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

@Slf4j
@SpringBootTest(classes = {FoodApiService.class, RestTemplateConfig.class})
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
@MapperScan("com.fitfusion.mapper")
class FoodApiServiceTest {

    @Autowired
    private FoodApiService foodApiService;

    @MockBean
    private com.fitfusion.mapper.UserMapper userMapper;

    @BeforeEach
    void beforeTest() {
        log.info("===============================================================");
        log.info("✅ 테스트 시작");
        log.info("===============================================================");
    }

    @AfterEach
    void afterTest() {
        log.info("===============================================================");
        log.info("✅ 테스트 종료");
        log.info("===============================================================");
    }

    /**
     * ✅ 운영 스타일 로그 테스트
     * - 키워드 리스트 기반으로 페이지별 데이터 수집 및 저장
     * - API URL, 페이지별 데이터 건수, DB 저장 결과 출력
     */
    @Test
    void testImportByKeyword() {
        List<String> keywords = List.of("말차", "거봉");

        for (String keyword : keywords) {
            printMainDivider();
            log.info("🚀 [{}] 데이터 수집 시작...", keyword);

            int totalFetched = 0;
            int totalSaved = 0;

            for (int page = 1; page <= 10; page++) { // 테스트용 → 최대 10페이지
                printSubDivider();
                log.info("✅ [{}] - {}페이지 데이터 수집 시작...", keyword, page);

                String url = buildFakeApiUrl(keyword, page);
                log.info("🌐 API 요청 URL: {}", url);

                List<FoodDto> pageData = foodApiService.fetchPage(keyword, page);
                if (pageData.isEmpty()) {
                    log.info("✅ '{}' 검색 종료: {}페이지에는 데이터 없음", keyword, page);
                    break;
                }

                totalFetched += pageData.size();
                log.info("📦 [{}] {}페이지 데이터 수집 완료 → {}건", keyword, page, pageData.size());

                FoodSaveResult result = foodApiService.saveFoods(pageData);
                totalSaved += result.getSaved();
                log.info("📦 [{}] DB 저장 결과 → 성공: {}, 중복: {}, 실패: {}",
                        keyword, result.getSaved(), result.getDuplicated(), result.getFailed());

                try {
                    Thread.sleep(200); // 요청 간격 유지 (API 부하 방지)
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error("테스트 중단됨");
                    break;
                }
            }

            log.info("📊 [{}] 데이터 수집 종료 → 총 {}건 (저장: {}건)", keyword, totalFetched, totalSaved);
            printMainDivider();
        }
    }

    // ----------------- 헬퍼 메서드 -----------------
    private void printMainDivider() {
        log.info("======================================================================================");
    }

    private void printSubDivider() {
        log.info("────────────────────────────────────────────────────────────────────");
    }

    private String buildFakeApiUrl(String keyword, int page) {
        return String.format("https://apis.data.go.kr/...FOOD_NM_KR=%s&pageNo=%d", keyword, page);
    }
}
