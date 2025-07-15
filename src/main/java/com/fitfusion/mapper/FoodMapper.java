package com.fitfusion.mapper;

import com.fitfusion.dto.FoodDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FoodMapper {

    // 전체 조회
    List<FoodDto> findAll();

    // 키워드 검색 (음식명 LIKE)
    List<FoodDto> searchFoods(@Param("keyword") String keyword);

    // 분류 검색 (대분류 기준)
    List<FoodDto> findByCategory(@Param("categoryCode") String categoryCode);

    // 수입 여부 검색
    List<FoodDto> findByImported(@Param("isImported") String isImported);

    // 신규 저장
    int insertFood(FoodDto foodDto);

    // 수정
    int updateFood(FoodDto foodDto);

    // 삭제
    int deleteFood(@Param("foodId") String foodId);

    // 키워드 검색 + 페이징
    List<FoodDto> searchFoodsByPaging(
            @Param("keyword") String keyword,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize
    );

    // 전체 개수 구하기
    int countFoodsByKeyword(@Param("keyword") String keyword);

    /**
     * DB에 저장된 모든 음식명을 조회한다.
     * - 중복 여부 체크나 전체 데이터 비교용으로 사용한다.
     *
     * @return 음식명(FOOD_NAME) 목록
     */
    List<String> findAllFoodNames();
}
