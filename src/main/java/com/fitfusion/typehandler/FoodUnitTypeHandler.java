package com.fitfusion.typehandler;

import com.fitfusion.enums.FoodUnit;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(FoodUnit.class)        // FoodUnit 타입 필드에만 적용
@MappedJdbcTypes(JdbcType.VARCHAR)  // DB 컬럼이 VARCHAR일 때 적용
public class FoodUnitTypeHandler extends BaseTypeHandler<FoodUnit> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, FoodUnit parameter, JdbcType jdbcType) throws SQLException {
        // DB에 저장할 때 Enum의 symbol을 사용
        ps.setString(i, parameter.getSymbol());
    }

    @Override
    public FoodUnit getNullableResult(ResultSet rs, String columnName) throws SQLException {
        // DB에서 읽어올 때 symbol을 FoodUnit Enum으로 변환
        String symbol = rs.getString(columnName);
        return symbol == null ? null : FoodUnit.fromSymbol(symbol);
    }

    @Override
    public FoodUnit getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String symbol = rs.getString(columnIndex);
        return symbol == null ? null : FoodUnit.fromSymbol(symbol);
    }

    @Override
    public FoodUnit getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String symbol = cs.getString(columnIndex);
        return symbol == null ? null : FoodUnit.fromSymbol(symbol);
    }
}

