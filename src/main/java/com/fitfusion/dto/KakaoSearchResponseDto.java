package com.fitfusion.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class KakaoSearchResponseDto {

    public List<KakaoplaceDto> documents;
}
