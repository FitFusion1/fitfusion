package com.fitfusion.service;

import com.fitfusion.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public boolean checkExists(String type) {
        if ("username".equals(type)) {
            return userMapper.checkUsernameExists(type);
        }
        if ("email".equals(type)) {
            return userMapper.checkEmailExists(type);
        }
        return false;
    }
}
