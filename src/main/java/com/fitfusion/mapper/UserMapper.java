package com.fitfusion.mapper;

import com.fitfusion.vo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    int checkUsernameExists(String username);
    int checkEmailExists(String email);
    void insertUser(User user);
    void insertUserRole(@Param("userId") int userId,
                        @Param("roleName") String roleName);
}
