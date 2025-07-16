package com.fitfusion.service;

import com.fitfusion.mapper.AdminMapper;
import com.fitfusion.vo.Notice;
import com.fitfusion.vo.User;
import com.fitfusion.web.form.AdminNoticeForm;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private ModelMapper modelMapper;

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
            throw new RuntimeException("다른 사람의 게시글은 삭제할 수 없습니다.");
        }

        adminMapper.softDeleteUserById(no);
    }

    public void softRestoreUserById(int no) {
        User user = adminMapper.getUserById(no);

        if (user == null){
            throw new RuntimeException("유저가 존재하지 않습니다.");
        }
        if (user.getUserId() != no) {
            throw new RuntimeException("다른 사람의 계정을 복구할 수 없습니다.");
        }

        adminMapper.softRestoreUserById(no);
    }

    public List<Notice> getAllNotices() {
        return adminMapper.getAllNotices();
    }

    public Notice getNoticeById(int id) {
        return adminMapper.getNoticeById(id);
    }

    public void insertNotice(AdminNoticeForm form, int id) {
        Notice notice = modelMapper.map(form, Notice.class);

        User user = new User();
        user.setUserId(id);

        notice.setUser(user);

        adminMapper.insertNotice(notice);
    }

    public int countNotices() {
        return adminMapper.countNotices();
    }

    public void deleteNoticeById(int id) {
        adminMapper.deleteNoticeById(id);
    }

    public void modifyNotice(AdminNoticeForm form, int id) {
        Notice notice = modelMapper.map(form, Notice.class);

        User user = new User();
        user.setUserId(id);

        notice.setUser(user);

        System.out.println("adminservice: " + notice.toString());

        adminMapper.modifyNotice(notice);
    }

}
