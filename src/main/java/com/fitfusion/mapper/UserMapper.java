package com.fitfusion.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    boolean checkUsernameExists(String username);
    boolean checkEmailExists(String email);
}
