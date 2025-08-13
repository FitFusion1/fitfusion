package com.fitfusion.service;

import com.fitfusion.exception.UserRegisterException;
import com.fitfusion.mapper.UserMapper;
import com.fitfusion.vo.User;
import com.fitfusion.web.form.UserEditForm;
import com.fitfusion.web.form.UserRegisterForm;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
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

    public User getUserById(int id) {
        return userMapper.getUserById(id);
    }

    public UserEditForm getUserEditForm(User user) {
        return modelMapper.map(user, UserEditForm.class);
    }

    public UserEditForm updateUserInfo(UserEditForm form) {
        System.out.println(form.getUserId());
        User newUserInfo = modelMapper.map(form, User.class);
        userMapper.updateUser(newUserInfo);
        User updatedUser = userMapper.getUserById(newUserInfo.getUserId());
        return modelMapper.map(updatedUser, UserEditForm.class);
    }

    public boolean validateExistingPassword(String passwordInput, int userId) {
        User existingUser = userMapper.getUserById(userId);
        return passwordEncoder.matches(passwordInput, existingUser.getPassword());
    }

    public void updatePassword(String passwordInput, int userId) {
        User existingUser = userMapper.getUserById(userId);
        existingUser.setPassword(passwordEncoder.encode(passwordInput));
        userMapper.updateUserPassword(existingUser);
    }
}
