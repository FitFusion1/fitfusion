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

    public User getUserByIdWithRoleNames(int id) {
        return adminMapper.getUserByIdWithRoleNames(id);
    }

    public User getUserById(int id) {
        return adminMapper.getUserById(id);
    }

    public List<User> getAllUsers() {
        return adminMapper.getAllUsers();
    }

    public int countActiveUsers() {
        return adminMapper.countActiveUsers();
    }

    public int countDeletedUsers() {
        return adminMapper.countDeletedUsers();
    }

    public int countTodayUsers() {
        return adminMapper.countTodayUsers();
    }

    public void softDeleteUserById(int no) {
        User user = adminMapper.getUserById(no);

        if (user == null){
            throw new RuntimeException("유저가 존재하지 않습니다.");
        }
        if (user.getUserId() != no) {
            throw new RuntimeException("다른 사람의 게시글은 수정할 수 없습니다.");
        }

        adminMapper.softDeleteUserById(no);
    }

    public void softRestoreUserById(int no) {
        User user = adminMapper.getUserById(no);

        if (user == null){
            throw new RuntimeException("유저가 존재하지 않습니다.");
        }
        if (user.getUserId() != no) {
            throw new RuntimeException("다른 사람의 게시글은 수정할 수 없습니다.");
        }

        adminMapper.softRestoreUserById(no);
    }
}
