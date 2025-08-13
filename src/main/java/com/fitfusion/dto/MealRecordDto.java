package com.fitfusion.dto;

import com.fitfusion.enums.FoodUnit;
import com.fitfusion.enums.MealTime;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.DecimalFormat;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealRecordDto {
        private Integer mealRecordId;  // DB에서 생성되는 PK (nullable)
        private Integer userId;        // 사용자 ID (필수)
        private Integer foodId;        // 음식 ID  체크박스 null 허용
        private MealTime mealTime;     // ex: BREAKFAST, LUNCH
        private Double userIntake;     // 사용자가 입력한 섭취량 (g, ml)

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private Date recordDate;       // 기록 날짜
        private Date createdDate;      // 생성일
        private Date updatedDate;      // 수정일

        // 화면 표시용 추가 필드 (optional)
        private String foodName;
        private String makerName;
        private String imageUrl;

        // 기본 제공량 (FoodDto에서 가져옴)
        private Double foodServingSizeValue;
        private FoodUnit foodServingSizeUnit;

        private Double foodWeightValue;
        private FoodUnit foodWeightUnit;

        //영양소
        private Double calories;        // 열량 (kcal)
        private Double carbohydrateG;   // 탄수화물 (g)
        private Double sugarG;          // 당류 (g)
        private Double sugarAlcoholG;   // 당알콜 (g)
        private Double fiberG;          // 식이섬유 (g)
        private Double proteinG;        // 단백질 (g)
        private Double fatG;            // 지방 (g)
        private Double saturatedFatG;   // 포화지방 (g)
        private Double transFatG;       // 트랜스지방 (g)
        private Double cholesterolMg;  // 콜레스테롤 (mg)
        private Double unsaturatedFatG; // 불포화지방산 (g)
        private Double sodiumMg;        // 나트륨 (mg)
        private Double caffeineMg;      // 카페인 (mg)
        private Double calciumMg;       // 칼슘 (mg)

        /**
         * 숫자를 소수점 2자리까지 포맷팅 (스레드 안전)
         * null일 경우 빈 문자열 반환
         */
        private String formatDouble(Double value) {
                if (value == null) return "";
                DecimalFormat df = new DecimalFormat("#.##");
                return df.format(value);
        }

        /**
         * 서빙 사이즈 수치값을 포맷팅하여 문자열로 반환
         */
        public String getFormattedServingSize() {
                return formatDouble(foodServingSizeValue);
        }

        /**
         * 서빙 사이즈 단위의 심볼을 반환
         * 단위가 null이면 빈 문자열 반환
         */
        public String getFoodServingSizeUnitSymbol() {
                return (foodServingSizeUnit != null) ? foodServingSizeUnit.getSymbol() : "";
        }

        public String getFormattedUserIntake() {
                return formatDouble(userIntake);
        }
}

