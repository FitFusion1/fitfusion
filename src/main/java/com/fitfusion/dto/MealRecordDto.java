package com.fitfusion.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
public class MealRecordDto {
        private int recordId;
        private int userId;
        private String mealTime; // ex: BREAKFAST, LUNCH
        private Double calories;
        private Double carbohydrateG;
        private Double proteinG;
        private Double fatG;
        private Date recordDate;
}
