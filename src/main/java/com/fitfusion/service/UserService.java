package com.fitfusion.service;

import com.fitfusion.exception.UserRegisterException;
import com.fitfusion.mapper.UserMapper;
import com.fitfusion.vo.User;
import com.fitfusion.web.form.UserRegisterForm;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean checkExists(String type, String value) {
        if ("username".equals(type)) {
            return (userMapper.checkUsernameExists(value) == 1);
        }
        if ("email".equals(type)) {
            return userMapper.checkEmailExists(value) == 1;
        }
        return false;
    }

    public User registerUser(UserRegisterForm form) {
        User foundUser = userMapper.getUserByUsername(form.getUsername());
        if (foundUser != null) {
            throw new UserRegisterException("username", "이미 사용중인 아이디입니다.");
        }
        foundUser = userMapper.getUserByEmail(form.getEmail());
        if (foundUser != null) {
            throw new UserRegisterException("email", "이미 사용중인 이메일입니다.");
        }

        User user = modelMapper.map(form, User.class);
        user.setPassword(passwordEncoder.encode(form.getPassword()));

        userMapper.insertUser(user);
        userMapper.insertUserRole(user.getUserId(), "ROLE_USER");

        return user;
    }
}
