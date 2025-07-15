package com.fitfusion.vo;

import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.Alias;

@Getter
@Setter
@Alias("UserRole")
public class UserRole {
    private int id;
    private int roleName;
}
