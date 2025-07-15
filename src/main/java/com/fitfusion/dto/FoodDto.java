package com.fitfusion.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FoodDto {

    private Integer foodId;                      // 식품코드
    private String barcode;                     // 바코드
    private String groupName;                   // 그룹명 (예: 찜류)
    @NotBlank
    private String foodName;                    // 식품명 (예: 찐고구마)

    // 식품 분류
    private String foodCategoryCode;            // 식품 대분류 코드
    private String foodCategoryName;            // 식품 대분류 명
    private String foodMidCategoryCode;         // 식품 중분류 코드
    private String foodMidCategoryName;         // 식품 중분류 명
    private String foodSmallCategoryCode;       // 식품 소분류 코드
    private String foodSmallCategoryName;       // 식품 소분류 명
    private String foodDetailCategoryCode;      // 식품 세분류 코드
    private String foodDetailCategoryName;      // 식품 세분류 명

    // 용량 정보
    private String servingSizeRaw;              // API 원본값 (예: "100g")
    private Double servingSize;                 // 수치만 추출된 값 (예: 100.0)
    private Integer servingUnitId;              // 단위 FK ID
    private Double foodWeight;                  // 식품 자체 중량 (예: 200g)
    private Double referenceIntake;             // 1회 섭취 참고량 (예: 200g)

    // 영양 정보
    private Double calories;                    // 열량(kcal)
    private Double carbohydrateG;               // 탄수화물(g)
    private Double proteinG;                    // 단백질(g)
    private Double fatG;                        // 지방(g)
    private Double sugarG;                      // 당류(g)
    private Double sodiumMg;                    // 나트륨(mg)
    private Double cholesterolMg;               // 콜레스테롤(mg)
    private Double saturatedFatG;               // 포화지방산(g)
    private Double transFatG;                   // 트랜스지방산(g)
    private Double potassiumMg;                 // 칼륨(mg)
    private Double fiberG;                      // 식이섬유(g)
    private Double ironMg;                      // 철분(mg)
    private Double calciumMg;                   // 칼슘(mg)
    private Double vitaminCMg;                  // 비타민 C(mg)
    private Double sugarAlcoholG;               // 당알콜(g)
    private Double caffeineMg;                  // 카페인(mg)
    private Double alcoholG;                    // 알코올(g)

    // 기타 정보
    private String imageUrl;                    // 이미지 URL
    private String isUserCreated;               // 사용자 생성 여부 (Y/N)
    private Long createdByUserId;               // 생성한 사용자 ID
    private Date createdDate;                   // 시스템 데이터 생성일자

    // 출처 정보
    private String dataSourceName;              // 데이터 출처명 (예: 식약처)
    private Date dataCreatedDate;               // 데이터 생성일자 (공공데이터 원본 생성일자)
    private Date dataModifiedDate;              // 데이터 수정일자 (공공데이터 갱신일자)
    private String dataType;                    // 데이터 구분명 (예: 음식, 영양소)

    // 유통 정보
    private String makerName;                   // 제조사명
    private String importerName;                // 수입업체명
    private String distributorName;             // 유통업체명
    private String originCountryName;           // 원산지 국가명
    private String isImported;                  // 수입 여부 (Y/N)

}
