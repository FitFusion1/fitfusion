package com.fitfusion.mapper;

import com.fitfusion.dto.FoodDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FoodMapper {

    /**
     * 모든 음식 목록 조회 (단위명 포함)
     */
    List<FoodDto> findAll();

    /**
     * 특정 음식 상세 조회 (단위명 포함)
     */
    FoodDto findFoodByIdWithUnit(@Param("foodId") Integer foodId);

    /**
     * 음식명 LIKE 검색
     */
    List<FoodDto> searchFoods(@Param("keyword") String keyword);

    /**
     * 대분류 코드로 검색
     */
    List<FoodDto> findByCategory(@Param("categoryCode") String categoryCode);

    /**
     * 수입 여부로 검색
     */
    List<FoodDto> findByImported(@Param("isImported") String isImported);

    /**
     * 키워드 검색 + 페이징
     */
    List<FoodDto> searchFoodsByPaging(
            @Param("keyword") String keyword,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize
    );

    /**
     * 키워드별 데이터 개수
     */
    int countFoodsByKeyword(@Param("keyword") String keyword);

    /**
     * 새로운 음식 등록
     */
    int insertFood(FoodDto foodDto);

    /**
     * 음식 정보 수정
     */
    int updateFood(FoodDto foodDto);

    /**
     * 음식 삭제
     */
    int deleteFood(@Param("foodId") Integer foodId);

    /**
     * 모든 음식명 조회
     * (중복 체크 / 오토컴플릿용)
     */
    List<String> findAllFoodNames();
}
