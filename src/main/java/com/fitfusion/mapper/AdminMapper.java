package com.fitfusion.mapper;

import com.fitfusion.vo.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AdminMapper {

    /*
        모든 유저 정보를 가져온다.
     */
    List<User> getAllUsers();
}
