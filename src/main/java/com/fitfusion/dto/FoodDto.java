package com.fitfusion.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fitfusion.util.EmptyStringToNullDateDeserializer;
import com.fitfusion.util.EmptyStringToNullDoubleDeserializer;
import com.fitfusion.util.EmptyStringToNullStringDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodDto {

    // 식품코드 (FOOD_ITEM 테이블 PK) - 시퀀스 자동생성
    private Integer foodId;

    // 식품 고유 코드 (DB 필드명 : FOOD_CODE)
    @JsonProperty("FOOD_CD") // API의 "FOOD_CD" 필드를 해당 변수에 매핑
    @JsonDeserialize(using = EmptyStringToNullStringDeserializer.class)
    private String foodCode;

    // 바코드 -- 삭제함 (확장 가능성 때문에 넣었는데 API에 자료 없음)
    // private String foodBarcode;

    // 식품명 (DB 필드명 : FOOD_NAME) ex) 고구마_찐고구마, 고구마전, 도넛_고구마품은 꽈배기
    @JsonProperty("FOOD_NM_KR") // API 필드명
    @JsonDeserialize(using = EmptyStringToNullStringDeserializer.class)
    private String foodName;

    // 제조사명 (DB 필드명 : MAKER_NM)
    @JsonProperty("MAKER_NM")
    @JsonDeserialize(using = EmptyStringToNullStringDeserializer.class)
    private String makerName;

    // 데이터구분명 (DB 필드명 : FOOD_GROUP_NAME) ex) 음식, 가공식품, 원재료성
    @JsonProperty("DB_GRP_NM")
    @JsonDeserialize(using = EmptyStringToNullStringDeserializer.class)
    private String foodGroupName;

    //품목대표/상용제품 (DB 필드명 : FOOD_CLASS_NAME) ex) 품목대표, 상용제품
    @JsonProperty("DB_CLASS_NM")
    @JsonDeserialize(using = EmptyStringToNullStringDeserializer.class)
    private String foodClassName;

    // 식품 대분류 코드 (DB 필드명 : FOOD_CAT1_CODE) ex) 7, 9, 13
    @JsonProperty("FOOD_CAT1_CD")
    @JsonDeserialize(using = EmptyStringToNullStringDeserializer.class)
    private String foodCat1Code;

    // 식품 대분류명 (DB 필드명 : FOOD_CAT1_NAME) ex) 찜류, 전·적 및 부침류, 빵 및 과자류
    @JsonProperty("FOOD_CAT1_NM")
    @JsonDeserialize(using = EmptyStringToNullStringDeserializer.class)
    private String foodCat1Name;

    // 대표식품코드 (DB 필드명 : FOOD_REF_CODE) ex) 7128, 9409, 13568
    @JsonProperty("FOOD_REF_CD")
    @JsonDeserialize(using = EmptyStringToNullStringDeserializer.class)
    private String foodRefCode;

    // 대표식품명 (DB 필드명 : FOOD_REF_NAME) ex) 고구마, 고구마전, 고구마줄기, 도넛, 소보로빵
    @JsonProperty("FOOD_REF_NM")
    @JsonDeserialize(using = EmptyStringToNullStringDeserializer.class)
    private String foodRefName;

    // 식품 중분류 코드 (DB 필드명 : FOOD_CAT2_CODE) ex) 712818, 940900, 1356800
    @JsonProperty("FOOD_CAT2_CD")
    @JsonDeserialize(using = EmptyStringToNullStringDeserializer.class)
    private String foodCat2Code;

    // 식품 중분류명 (DB 필드명 : FOOD_CAT2_NAME) ex) 찐고구마, 해당없음, 해당없음(과자류, 빵류 또는 떡류), 영·유아용 이유식, 체중조절용 조제식품
    @JsonProperty("FOOD_CAT2_NM")
    @JsonDeserialize(using = EmptyStringToNullStringDeserializer.class)
    private String foodCat2Name;

    // 영양성분함량 기준이 되는 단위. 보통 100g
    // 영양성분함량기준량 (DB 필드명 : FOOD_SERVING_SIZE_RAW) ex) 100g
    @JsonProperty("SERVING_SIZE")
    @JsonDeserialize(using = EmptyStringToNullStringDeserializer.class)
    private String foodServingSizeRaw; // API 응답값 그대로 저장 (디버깅/백업용) ex) 100g

    // 영양성분함량기준량 중 수치값 (DB 필드명 : FOOD_SERVING_VALUE) ex) 100
    private Double foodServingSizeValue;

    // 영양성분함량기준량의 중 단위 ID (DB 필드명 : FOOD_SERVING_UNIT_ID) ex) g  (FOOD_UNITS 테이블에서 ID 매핑)
    private Integer foodServingUnitId;

    // 아직 서비스에 로직 추가 안 함. foodServingSizeValue랑 공통기능인데 메서드 못 쓰나
    // 식품 전체 중량 (DB 컬럼 FOOD_WEIGHT_RAW) ex) 150.000g, 1,000.000g, 591.000mL
    @JsonProperty("Z10500")
    @JsonDeserialize(using = EmptyStringToNullStringDeserializer.class)
    // API 응답값 그대로 저장 (디버깅/백업용) ex) 150.000g
    private String foodWeightRaw;

    // 식품 전체 중량 수치값 ex) 150.000
    private Double foodWeightValue;

    // 식품 전체 중량 단위 ID (DB 필드명 : FOOD_WEIGHT_ID) (단위 매핑 ID (FOOD_UNITS.FOOD_UNIT_ID)) (예: g → 1, mL → 2)
    private Integer foodWeightUnitId;

    // 1회 섭취참고량 (DB 컬럼 : REFERENCE_INTAKE) ex) '생·숙면 200g, 건면 100g, 당면 30g, 유탕면(봉지)120g, 유탕면(용기)80g', '드레싱 15g, 덮밥소스 165g', 80ml(g), 1식
    @JsonProperty("NUTRI_AMOUNT_SERVING")
    @JsonDeserialize(using = EmptyStringToNullStringDeserializer.class)
    private String referenceIntake;

    // 에너지(칼로리 Kcal) (DB 필드명 : CALORIES) ex) 139, 227, 83
    @JsonProperty("AMT_NUM1")
    @JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
    private Double calories;

    // 탄수화물 (DB 필드명 : CARBOHYDRATE_G) ex) 51, 31.3, 88.75
    @JsonProperty("AMT_NUM6")
    @JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
    private Double carbohydrateG;

    // 단백질 (DB 필드명 : PROTEIN_G) ex) 0.98, 1.67, 2.22
    @JsonProperty("AMT_NUM3")
    @JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
    private Double proteinG;

    // 지방 (DB 필드명 : FAT_G) ex)  0, 0.24, 7, 7.98
    @JsonProperty("AMT_NUM4")
    @JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
    private Double fatG;

    // 당류 (DB 필드명 : SUGAR_G) ex) 0, 0.09, 16.32
    @JsonProperty("AMT_NUM7")
    @JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
    private Double sugarG;

    // 식이섬유 (DB 필드명 : FIBER_G) ex) 0.8, 2.9, 35.4
    @JsonProperty("AMT_NUM8")
    @JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
    private Double fiberG;

    // 칼슘 (DB 필드명 :CALCIUM_MG) ex) 1, 20, 210, 947
    @JsonProperty("AMT_NUM9")
    @JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
    private Double calciumMg;

    // 철 (DB 필드명 : IRON_MG) ex) 0.15, 0.51, 1.1
    @JsonProperty("AMT_NUM10")
    @JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
    private Double ironMg;

    // 칼륨 (DB 필드명 : POTASSIUM_MG) ex) 2, 122, 559
    @JsonProperty("AMT_NUM12")
    @JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
    private Double potassiumMg;

    // 나트륨 (DB 필드명 : SODIUM_MG) ex) 0, 10, 344, 1,263.00
    @JsonProperty("AMT_NUM13")
    @JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
    private Double sodiumMg;

    // 비타민 C (DB 필드명 : VITAMIN_C_MG) ex) 0, 10.47, 277.11
    @JsonProperty("AMT_NUM21")
    @JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
    private Double vitaminCMg;

    // 콜레스테롤 (DB 필드명 : CHOLESTEROL_MG) ex) 0, 23, 182.53
    @JsonProperty("AMT_NUM23")
    @JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
    private Double cholesterolMg;

    // 포화지방산 (DB 필드명 : SATURATED_FAT_G) ex) 0, 0.01, 23.33
    @JsonProperty("AMT_NUM24")
    @JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
    private Double saturatedFatG;

    // 트랜스지방산 (DB 필드명 : TRANS_FAT_G) ex) 0, 0.01, 4.8
    @JsonProperty("AMT_NUM25")
    @JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
    private Double transFatG;

    // 총 불포화지방산 (DB 필드명 : UNSATURATED_FAT_G) ex) 0, 0.01, 0.46
    @JsonProperty("AMT_NUM61")
    @JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
    private Double unsaturatedFatG;

    // 총 지방산 (DB 필드명 : TOTAL_FATTY_ACIDS_G) ex) 0.02, 0.58
    @JsonProperty("AMT_NUM154")
    @JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
    private Double totalFattyAcidsG;

    // 당알콜 (API 필드명 AMT_NUM53) ex) 0.42
    @JsonProperty("AMT_NUM53")
    @JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
    private Double sugarAlcoholG;

    // 카페인 (API 필드명 AMT_NUM100) ex) 0
    @JsonProperty("AMT_NUM100")
    @JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
    private Double caffeineMg;

    // 원산지국명 (DB 필드명 : ORIGIN_COUNTRY_NAME) ex) 말레이시아, 태국, 일본
    @JsonProperty("NATION_NM")
    @JsonDeserialize(using = EmptyStringToNullStringDeserializer.class)
    private String originCountryName;

    // 수입여부 (DB 필드명 : IS_IMPORTED) ex) Y, N
    @JsonProperty("IMP_YN")
    @JsonDeserialize(using = EmptyStringToNullStringDeserializer.class)
    private String isImported;

    // 아직 사진 못 구함
    // 이미지 URL (DB 필드명 : IMAGE_URL)
    private String imageUrl;

    // 음식 DB 유저 생성 여부 (DB 필드명 : IS_USER_CREATED)
    private String isUserCreated;

    // 음식 DB 생성 유저ID (DB 필드명 : CREATED_BY_USER_ID) , Default : N
    private Integer createdByUserId;

    // 데이터 출처명 (DB 필드명 : DATA_SOURCE_NAME) ex) 식품의약품안전처(어느 API 자료인지)
    private String dataSourceName;

    // 데이터 수정일자 (DB 필드명 : API_RECORD_UPDATED_DATE) ex) 2025-02-11, 2025-05-08
    @JsonProperty("UPDATE_DATE")
    @JsonDeserialize(using = EmptyStringToNullDateDeserializer.class)
    private Date apiRecordUpdateDate;

    // 생성일자 (DB 필드명 : CREATED_DATE)
    private Date createdDate;

    // 수정일자 (DB 필드명 : UPDATED_DATE)
    private Date updatedDate;

//    //삭제함
//    //수입업체명 (API 필드명 IMP_MANUFAC_NM)
//    @JsonProperty("IMP_MANUFAC_NM")
//    @JsonDeserialize(using = EmptyStringToNullStringDeserializer.class)
//    private String importerName;

//    //삭제함
//    //유통업체명 (API 필드명 SELLER_MANUFAC_NM)
//    @JsonProperty("SELLER_MANUFAC_NM")
//    @JsonDeserialize(using = EmptyStringToNullStringDeserializer.class)
//    private String distributorName;

//    //삭제함
//    //식품 소분류 코드 (API 필드명 FOOD_CAT3_CD)
//    @JsonProperty("FOOD_CAT3_CD")
//    @JsonDeserialize(using = EmptyStringToNullStringDeserializer.class)
//    private String foodSmallCategoryCode;

//    //삭제함
//    //식품 소분류명 (API 필드명 FOOD_CAT3_NM)
//    @JsonProperty("FOOD_CAT3_NM")
//    @JsonDeserialize(using = EmptyStringToNullStringDeserializer.class)
//    private String foodSmallCategoryName;

//    //삭제함
//    //식품 세분류 코드 (API 필드명 FOOD_CAT4_CD)
//    @JsonProperty("FOOD_CAT4_CD")
//    @JsonDeserialize(using = EmptyStringToNullStringDeserializer.class)
//    private String foodDetailCategoryCode;

//    //삭제함
//    //식품 세분류명 (API 필드명 FOOD_CAT4_NM)
//    @JsonProperty("FOOD_CAT4_NM")
//    @JsonDeserialize(using = EmptyStringToNullStringDeserializer.class)
//    private String foodDetailCategoryName;

//    //삭제함 - 있으면 좋은데 API 자료에 없음
//    //알코올 (API 필드명 AMT_NUM154)
//    @JsonProperty("AMT_NUM154")
//    @JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
//    private Double alcoholG;

}
