package com.fitfusion.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.fitfusion.vo.User;

@Mapper
public interface UserMapper {

    List<User> getUsers();

    void insertUser(User user);

    void insertUserRole(@Param("userNo") int userNo,
                        @Param("roleName") String roleName);

    User getUserByUsername(String username);

    User getUserByUsernameWithRoleNames(String username);

    User getUserByEmail(String email);

    User getUserByNo(int userNo);

    List<String> getRoleNamesByUserNo(int userNo);
}