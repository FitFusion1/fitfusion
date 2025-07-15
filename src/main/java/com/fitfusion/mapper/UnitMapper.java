package com.fitfusion.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 단위 정보를 조회하기 위한 Mapper
 */
@Mapper
public interface UnitMapper {

    /**
     * 단위 심볼로 UNIT_ID를 조회
     * 예) "g" → 1
     */
    @Select("SELECT UNIT_ID FROM FITFUSION_UNITS WHERE LOWER(UNIT_SYMBOL) = LOWER(#{unitSymbol})")
    Integer findUnitIdBySymbol(@Param("unitSymbol") String unitSymbol);
}
