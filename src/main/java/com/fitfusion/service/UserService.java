package com.fitfusion.service;

import com.fitfusion.mapper.UserMapper;
import com.fitfusion.vo.User;
import com.fitfusion.web.form.UserRegisterForm;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
        User user = modelMapper.map(form, User.class);
        user.setPassword(passwordEncoder.encode(form.getPassword()));

        userMapper.insertUser(user);
        userMapper.insertUserRole(user.getUserId(), "ROLE_USER");

        return user;
    }
}
