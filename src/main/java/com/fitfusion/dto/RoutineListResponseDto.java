package com.fitfusion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoutineListResponseDto {
    private List<RoutineListDto> data;
    private Meta meta;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Meta {
        private int page;
        private int size;
        private long totalElements;
        private int totalPage;
    }
}
