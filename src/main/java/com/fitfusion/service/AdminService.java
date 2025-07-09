package com.fitfusion.service;

import com.fitfusion.mapper.AdminMapper;
import com.fitfusion.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private AdminMapper adminMapper;

    public List<User> getAllUsers() {
        return adminMapper.getAllUsers();
    }
}
