package com.fitfusion.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoplaceDto {

    @JsonProperty("place_name")
    private String placeName;

    @JsonProperty("address_name")
    private String addressName;

    @JsonProperty("place_url")
    private String profileUrl;

    @JsonProperty("distance")
    private String distance;

    @JsonProperty("road_address_name")
    private String roadAddressName;
    private String phone;
    private String x;
    private String y;
}

