//package com.fitfusion.dto;
//
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import com.fasterxml.jackson.annotation.JsonProperty;
//import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
//import com.fitfusion.util.EmptyStringToNullDoubleDeserializer;
//import jakarta.validation.constraints.NotBlank;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.util.Date;
//
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@JsonIgnoreProperties(ignoreUnknown = true)
//public class OldFoodDto {
//
//    //@JsonProperty 없음 - DB sequence 사용
//    private Integer foodId;                      // 식품코드 (DB PK)
//
//    @JsonProperty("FOOD_CD")
//    private String foodCd;                      //  식품 영양 API의 식품 고유 코드
//
//    @JsonProperty("BARCODE")
//    private String barcode;                     // 바코드
//
//    @NotBlank
//    @JsonProperty("FOOD_NM_KR")
//    private String foodName;                    // 식품명 (예: 찐고구마)
//
//    @JsonProperty("MAKER_NM")
//    private String makerName;                   // 제조사명
//
//    @JsonProperty("SERVING_SIZE")
//    //@JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
//    private String servingSize;                 // 1회 섭취 기준량 (예: 100.0)
//
//    // 영양 정보
//    @JsonProperty("AMT_NUM1")
//    @JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
//    private Double calories;
//
//    @JsonProperty("AMT_NUM6")
//    @JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
//    private Double carbohydrateG;
//
//    @JsonProperty("AMT_NUM3")
//    @JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
//    private Double proteinG;
//
//    @JsonProperty("AMT_NUM4")
//    @JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
//    private Double fatG;
//
//    @JsonProperty("AMT_NUM7")
//    @JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
//    private Double sugarG;
//
//    @JsonProperty("AMT_NUM13")
//    @JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
//    private Double sodiumMg;
//
//    @JsonProperty("AMT_NUM23")
//    @JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
//    private Double cholesterolMg;
//
//    @JsonProperty("AMT_NUM24")
//    @JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
//    private Double saturatedFatG;
//
//    @JsonProperty("AMT_NUM25")
//    @JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
//    private Double transFatG;
//
//    @JsonProperty("AMT_NUM12")
//    @JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
//    private Double potassiumMg;
//
//    @JsonProperty("AMT_NUM8")
//    @JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
//    private Double fiberG;
//
//    private String imageUrl;
//
//    private String isUserCreated;
//
//    private Long createdByUserId;
//
//    private Date createdDate;
//
//    // 식품 분류
//    @JsonProperty("FOOD_CAT1_CD")
//    private String foodCategoryCode;
//
//    @JsonProperty("FOOD_CAT1_NM")
//    private String foodCategoryName;
//
//    @JsonProperty("FOOD_CAT2_CD")
//    private String foodMidCategoryCode;
//
//    @JsonProperty("FOOD_CAT2_NM")
//    private String foodMidCategoryName;
//
//    @JsonProperty("FOOD_CAT3_CD")
//    private String foodSmallCategoryCode;
//
//    @JsonProperty("FOOD_CAT3_NM")
//    private String foodSmallCategoryName;
//
//    @JsonProperty("FOOD_CAT4_CD")
//    private String foodDetailCategoryCode;
//
//    @JsonProperty("FOOD_CAT4_NM")
//    private String foodDetailCategoryName;
//
//    @JsonProperty("Z10500")
//    @JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
//    private Double foodWeight;
//
//    @JsonProperty("NUTRI_AMOUNT_SERVING")
//    @JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
//    private Double referenceIntake;
//
//    @JsonProperty("SUB_REF_NAME")
//    private String dataSourceName;
//
//    @JsonProperty("UPDATE_DATE")
//    private Date dataModifiedDate;
//
//    @JsonProperty("DB_CLASS_NM")
//    private String dataType;
//
//    @JsonProperty("IMP_MANUFAC_NM")
//    private String importerName;
//
//    @JsonProperty("SELLER_MANUFAC_NM")
//    private String distributorName;
//
//    @JsonProperty("NATION_NM")
//    private String originCountryName;
//
//    @JsonProperty("IMP_YN")
//    private String isImported;
//
//    @JsonProperty("AMT_NUM10")
//    @JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
//    private Double ironMg;
//
//    @JsonProperty("AMT_NUM9")
//    @JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
//    private Double calciumMg;
//
//    @JsonProperty("AMT_NUM21")
//    @JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
//    private Double vitaminCMg;
//
//    @JsonProperty("AMT_NUM53")
//    @JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
//    private Double sugarAlcoholG;
//
//    @JsonProperty("AMT_NUM100")
//    @JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
//    private Double caffeineMg;
//
//    @JsonProperty("AMT_NUM154")
//    @JsonDeserialize(using = EmptyStringToNullDoubleDeserializer.class)
//    private Double alcoholG;
//
//    private Integer servingUnitId;
//}
