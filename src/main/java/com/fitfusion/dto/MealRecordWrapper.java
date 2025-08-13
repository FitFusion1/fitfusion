package com.fitfusion.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MealRecordWrapper {
    private List<MealRecordDto> mealRecords = new ArrayList<>();
}

