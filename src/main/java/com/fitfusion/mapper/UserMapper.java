package com.fitfusion.mapper;

import com.fitfusion.vo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mapper
public interface UserMapper {

    int checkUsernameExists(String username);
    int checkEmailExists(String email);
    void insertUser(User user);
    void insertUserRole(@Param("userId") int userId,
                        @Param("roleName") String roleName);

    /**
     * 아이디 혹은 이메일과 그 유형을 전달받아서
     * 유저 객체를 전달받는다.
     * @param condition {type: username 혹은 email, identifier: 값}
     * @return 유저 객체
     */
    Optional<User> getUserWithRoleNames(Map<String, Object> condition);
    List<String> getRoleNamesByUserId(int id);
    User getUserByUsername(String username);
    User getUserByEmail(String email);
    User getUserById(int id);
    void updateUser(User user);
    void updateUserPassword(User user);

}
