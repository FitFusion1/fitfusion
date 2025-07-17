package com.fitfusion.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fitfusion.util.EmptyStringToNullDoubleDeserializer;
import com.fitfusion.util.EmptyStringToNullStringDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodDto {

    // 식품코드 (DB PK) - 시퀀스 자동생성
    private Integer foodId;

    // 식품 고유 코드 (API 필드명 FOOD_CD)
    @JsonProperty("FOOD_CD")
    @JsonDeserialize(using = EmptyStringToNullStringDeserializer.class)
    private String foodCd;

    // 바코드
    private String barcode;

    // 식품명 (API 필드명 FOOD_NM_KR)
    @JsonProperty("FOOD_NM_KR")
    @JsonDeserialize(using = EmptyStringToNullStringDeserializer.class)
    private String foodName;

    // 제조사명 (API 필드명 MAKER_NM)
    @JsonProperty("MAKER_NM")
    @JsonDeserialize(using = EmptyStringToNullStringDeserializer.class)
    private String makerName;

    // 그룹명 (DB 컬럼 GROUP_NAME)
    private String groupName;

    // 1회 섭취 기준량 (API 필드명 SERVING_SIZE) - 원본 문자열
    @JsonProperty("SERVING_SIZE")
    @JsonDeserialize(using = EmptyStringToNullStringDeserializer.class)
    private String servingSizeValue;

    // 칼로리 (API 필드명 AMT_NUM1)
    @JsonProperty("AMT_NUM1")
    @JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
    private Double calories;

    // 탄수화물 (API 필드명 AMT_NUM6)
    @JsonProperty("AMT_NUM6")
    @JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
    private Double carbohydrateG;

    // 단백질 (API 필드명 AMT_NUM3)
    @JsonProperty("AMT_NUM3")
    @JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
    private Double proteinG;

    // 지방 (API 필드명 AMT_NUM4)
    @JsonProperty("AMT_NUM4")
    @JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
    private Double fatG;

    // 당류 (API 필드명 AMT_NUM7)
    @JsonProperty("AMT_NUM7")
    @JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
    private Double sugarG;

    // 나트륨 (API 필드명 AMT_NUM13)
    @JsonProperty("AMT_NUM13")
    @JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
    private Double sodiumMg;

    // 콜레스테롤 (API 필드명 AMT_NUM23)
    @JsonProperty("AMT_NUM23")
    @JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
    private Double cholesterolMg;

    // 포화지방산 (API 필드명 AMT_NUM24)
    @JsonProperty("AMT_NUM24")
    @JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
    private Double saturatedFatG;

    // 트랜스지방산 (API 필드명 AMT_NUM25)
    @JsonProperty("AMT_NUM25")
    @JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
    private Double transFatG;

    // 칼륨 (API 필드명 AMT_NUM12)
    @JsonProperty("AMT_NUM12")
    @JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
    private Double potassiumMg;

    // 식이섬유 (API 필드명 AMT_NUM8)
    @JsonProperty("AMT_NUM8")
    @JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
    private Double fiberG;

    // 이미지 URL
    private String imageUrl;

    // 사용자 생성 여부
    private String isUserCreated;

    // 생성자 ID
    private Integer createdByUserId;

    // 생성일자
    private Date createdDate;

    // 식품 대분류 코드 (API 필드명 FOOD_CAT1_CD)
    @JsonProperty("FOOD_CAT1_CD")
    @JsonDeserialize(using = EmptyStringToNullStringDeserializer.class)
    private String foodCategoryCode;

    // 식품 대분류명 (API 필드명 FOOD_CAT1_NM)
    @JsonProperty("FOOD_CAT1_NM")
    @JsonDeserialize(using = EmptyStringToNullStringDeserializer.class)
    private String foodCategoryName;

    // 식품 중분류 코드 (API 필드명 FOOD_CAT2_CD)
    @JsonProperty("FOOD_CAT2_CD")
    @JsonDeserialize(using = EmptyStringToNullStringDeserializer.class)
    private String foodMidCategoryCode;

    // 식품 중분류명 (API 필드명 FOOD_CAT2_NM)
    @JsonProperty("FOOD_CAT2_NM")
    @JsonDeserialize(using = EmptyStringToNullStringDeserializer.class)
    private String foodMidCategoryName;

    // 식품 소분류 코드 (API 필드명 FOOD_CAT3_CD)
    @JsonProperty("FOOD_CAT3_CD")
    @JsonDeserialize(using = EmptyStringToNullStringDeserializer.class)
    private String foodSmallCategoryCode;

    // 식품 소분류명 (API 필드명 FOOD_CAT3_NM)
    @JsonProperty("FOOD_CAT3_NM")
    @JsonDeserialize(using = EmptyStringToNullStringDeserializer.class)
    private String foodSmallCategoryName;

    // 식품 세분류 코드 (API 필드명 FOOD_CAT4_CD)
    @JsonProperty("FOOD_CAT4_CD")
    @JsonDeserialize(using = EmptyStringToNullStringDeserializer.class)
    private String foodDetailCategoryCode;

    // 식품 세분류명 (API 필드명 FOOD_CAT4_NM)
    @JsonProperty("FOOD_CAT4_NM")
    @JsonDeserialize(using = EmptyStringToNullStringDeserializer.class)
    private String foodDetailCategoryName;

    // 식품 중량 (DB 컬럼 FOOD_WEIGHT)
    private Double foodWeight;

    // 1회 섭취참고량 (DB 컬럼 REFERENCE_INTAKE)
    private Double referenceIntake;

    // 데이터 출처명 (DB 컬럼 DATA_SOURCE_NAME)
    private String dataSourceName;

    // 데이터 수정일자 (DB 컬럼 DATA_MODIFIED_DATE)
    private Date dataModifiedDate;

    // 데이터 타입 (DB 컬럼 DATA_TYPE)
    private String dataType;

    // 수입업체명 (API 필드명 IMP_MANUFAC_NM)
    @JsonProperty("IMP_MANUFAC_NM")
    @JsonDeserialize(using = EmptyStringToNullStringDeserializer.class)
    private String importerName;

    // 유통업체명 (API 필드명 SELLER_MANUFAC_NM)
    @JsonProperty("SELLER_MANUFAC_NM")
    @JsonDeserialize(using = EmptyStringToNullStringDeserializer.class)
    private String distributorName;

    // 원산지국명 (API 필드명 NATION_NM)
    @JsonProperty("NATION_NM")
    @JsonDeserialize(using = EmptyStringToNullStringDeserializer.class)
    private String originCountryName;

    // 수입여부 (API 필드명 IMP_YN)
    @JsonProperty("IMP_YN")
    @JsonDeserialize(using = EmptyStringToNullStringDeserializer.class)
    private String isImported;

    // 철 (API 필드명 AMT_NUM10)
    @JsonProperty("AMT_NUM10")
    @JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
    private Double ironMg;

    // 칼슘 (API 필드명 AMT_NUM9)
    @JsonProperty("AMT_NUM9")
    @JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
    private Double calciumMg;

    // 비타민 C (API 필드명 AMT_NUM21)
    @JsonProperty("AMT_NUM21")
    @JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
    private Double vitaminCMg;

    // 당알콜 (API 필드명 AMT_NUM53)
    @JsonProperty("AMT_NUM53")
    @JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
    private Double sugarAlcoholG;

    // 카페인 (API 필드명 AMT_NUM100)
    @JsonProperty("AMT_NUM100")
    @JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
    private Double caffeineMg;

    // 알코올 (API 필드명 AMT_NUM154)
    @JsonProperty("AMT_NUM154")
    @JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
    private Double alcoholG;

    // 1회 섭취 단위 ID (DB 컬럼 SERVING_UNIT_ID)
    private Integer servingUnitId;

}
