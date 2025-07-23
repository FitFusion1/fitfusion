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
    List<FoodDto> searchFoodsByKeyword(@Param("keyword") String keyword);

    /**
     * 식품 대분류 코드(FOOD_CAT1_CD)로 검색
     * 예: 찜류(7), 빵 및 과자류(2), 나물·숙채류(13) 등
     */
    List<FoodDto> findByFoodCat1Code(@Param("foodCat1Code") String foodCat1Code);

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
    int deleteFood(Integer foodId);

    /**
     * 모든 음식명 조회
     * (중복 체크 / 오토컴플릿용)
     */
    List<String> findAllFoodNames();

    /**
     * 해당 FOOD_CODE를 가진 음식이 이미 DB에 존재하는지 확인
     *
     * @param foodCode 확인할 식품 고유 코드
     * @return 존재하면 1 이상, 존재하지 않으면 0
     */
    int existsByFoodCode(String foodCode);


    FoodDto findById(Integer id);
}
