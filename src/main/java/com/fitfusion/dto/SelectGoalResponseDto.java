package com.fitfusion.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SelectGoalResponseDto {
    private String alert;
    private boolean requiresCondition;
}
