package com.fitfusion.mapper;

import com.fitfusion.dto.FoodDto;
import com.fitfusion.dto.SelectedFoodDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FoodMapper {

    // ==============================================================
    // [1] 조회 (SELECT)
    // ==============================================================

//    /**
//     * 음식 기본 목록 (UI 리스트용)
//     * - 최소 필드: ID, 이름, ServingSizeRaw, 칼로리
//     */
//    List<FoodDto> findAllBasic();

    /**
     * 음식명 LIKE 검색 (자동완성 등 간단 검색)
     *
     * @param keyword 검색 키워드
     * @return  FoodDto 리스트
     */
    List<FoodDto> searchFoodsByKeyword(@Param("keyword") String keyword);


    /**
     * 키워드 검색 + 페이징 (UI 검색 결과 화면)
     *- 최소 필드: ID, 이름, ServingSizeRaw, 칼로리...
     *
     * @param keyword 검색 키워드
     * @param offset 페이지 시작 위치
     * @param pageSize 페이지 크기
     * @return FoodDto 리스트
     */
    List<FoodDto> searchFoodsByPaging(
            @Param("keyword") String keyword,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize
    );

    /**
     * 키워드별 데이터 개수
     *
     * @param keyword 검색 키워드
     * @return 데이터 개수
     */
    int countFoodsByKeyword(@Param("keyword") String keyword);

    /**
     * 특정 음식 상세 조회
     *
     * @param foodId 음식 ID
     * @return FoodDto
     */
    FoodDto findFoodById(@Param("foodId") Integer foodId);

    /**
     * UI 전용: 선택된 음식 조회 (SelectedFoodDto)
     */
    List<SelectedFoodDto> findFoodsByIds(@Param("ids") List<Integer> ids);

    /**
     * ☆ 비즈니스 로직용: 전체 상세 정보 반환 (FoodDto)
     */
    List<FoodDto> findFoodsAsFoodDto(@Param("ids") List<Integer> ids);

//    /**
//     * 식품 대분류 코드(FOOD_CAT1_CD)로 검색  예: 찜류(7), 빵 및 과자류(2), 나물·숙채류(13) 등
//     *
//     * @param foodCat1Code 대분류 코드
//     * @return FoodDto 리스트
//     */
//    List<FoodDto> findByFoodCat1Code(@Param("foodCat1Code") String foodCat1Code);
//
//    // 자동완성용
//    List<String> findAllFoodNames();

//    /**
//     * 수입 여부로 검색 (Y/N)
//     * @param isImported Y/N
//     * @return FoodDto 리스트
//     */
//    List<FoodDto> findByImported(@Param("isImported") String isImported);

    /**
     * 전체 음식 목록 (풀 데이터: 배치용)
     */
    List<FoodDto> findAllForExport();

    // 즐겨찾기한 음식
    List<FoodDto> findFavoriteFoods(int userId);

    // 자주 찾은 음식
    List<FoodDto> findFrequentFoods(int userId);

    // ==============================================================
    // [2] 등록 (INSERT)
    // ==============================================================

    /**
     * 새로운 음식 등록
     *
     * @param foodDto 음식 DTO
     * @return INSERT 성공 시 1
     */
    int insertFood(FoodDto foodDto);

    // ==============================================================
    // [3] 수정 (UPDATE)
    // ==============================================================

    /**
     * 음식 정보 수정
     *
     * @param foodDto 음식 DTO
     * @return UPDATE 성공 시 1
     */
    int updateFood(FoodDto foodDto);

    // ==============================================================
    // [4] 삭제 (DELETE)
    // ==============================================================

    /**
     * 음식 삭제
     *
     * @param foodId 음식 ID
     * @return DELETE 성공 시 1
     */
    int deleteFood(Integer foodId);

    // ==============================================================
    // [5] 유틸/검증
    // ==============================================================

    /**
     * 해당 FOOD_CODE를 가진 음식이 이미 DB에 존재하는지 확인
     *
     * @param foodCode 식품 코드
     * @return 존재하면 1 이상, 존재하지 않으면 0
     */
    int existsByFoodCode(String foodCode);

}
