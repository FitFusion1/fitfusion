package com.fitfusion;

import com.fitfusion.config.RestTemplateConfig;
import com.fitfusion.dto.FoodDto;
import com.fitfusion.dto.FoodSaveResult;
import com.fitfusion.mapper.FoodMapper;
import com.fitfusion.service.FoodApiService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

/**
 * ✅ FoodApiServiceTest
 * - 공공데이터 API 연동 기능 및 DB 저장 기능 검증
 * - 실제 API 호출 (인터넷 필요)
 * - DB 저장 시 FoodMapper 사용 (DB 연결 필요)
 * *  실행 조건: 해당 api 키 존재,
 *              DTO에 알맞는 FITFUSION_FOOD_ITEMS 테이블 존재
 */



@Slf4j
@SpringBootTest(classes = {FoodApiService.class, RestTemplateConfig.class})
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
//@TestPropertySource(properties = {"logging.level.root=DEBUG"})
@MapperScan("com.fitfusion.mapper") // MyBatis Mapper 스캔 추가
class FoodApiServiceTest {

    @Autowired
    private FoodApiService foodApiService;

    @MockBean
    private com.fitfusion.mapper.UserMapper userMapper;

    /**
     *  API 첫 페이지 데이터 '조회' 테스트
     * - 특정 키워드로 API 호출
     * - 첫 페이지 결과를 가져와 음식명과 코드 출력
     */
    @Test
    void testFetchPreview() {
        String keyword = "거봉"; // 테스트용 키워드
        List<FoodDto> list = foodApiService.fetchPreview(keyword);
        log.info("✅ [{}] 첫 페이지 결과: {}건", keyword, list.size());

        list.forEach(food -> log.info(" - {} ({})", food.getFoodName(), food.getFoodCode()));
    }

    /**
     * DB 저장 기능 테스트
     * - 특정 키워드로 API에서 '첫 페이지' 데이터를 가져온 뒤
     * - FoodDto 리스트를 'DB에 저장' (FoodMapper 사용)
     * - 저장 성공, 중복, 실패 개수 출력
     */
    @Test
    void testSaveFoods() {
        String keyword = "거봉";
        List<FoodDto> list = foodApiService.fetchPreview(keyword);
        FoodSaveResult result = foodApiService.saveFoods(list);
        log.info("✅ 저장 결과 → {}", result);
    }

    /**
     * totalCount 조회 테스트
     * - API에서 특정 키워드로 검색 시 존재하는 전체 음식 개수를 반환
     * - DB 저장은 하지 않고 API의 totalCount 값만 확인
     */
    @Test
    void testTotalCount() {
        String keyword = "거봉";
        int count = foodApiService.getTotalCount(keyword);
        log.info("✅ [{}] totalCount: {}", keyword, count);
    }

    /**
     * Import 기능 테스트
     * - 키워드 리스트 기반으로 여러 페이지 데이터를 수집하고 DB에 저장
     * - 중복 제거 및 저장 건수, 요청 횟수 등 요약 출력
     * - 대량 데이터 저장 테스트 가능
     */
    @Test
    void testImportByKeyword() {
        List<String> keywords = List.of("거봉", "장어", "숙주"); // 여러 개 가능
        String summary = foodApiService.importByKeywords(keywords);
        log.info(summary);
    }
}
