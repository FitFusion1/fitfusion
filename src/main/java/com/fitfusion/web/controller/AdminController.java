package com.fitfusion.web.controller;

import com.fitfusion.service.AdminService;
import com.fitfusion.vo.Notice;
import com.fitfusion.vo.User;
import com.fitfusion.web.form.AdminNoticeForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping
    public String admin(Model model) {
        model.addAttribute("countActiveUsers", adminService.countActiveUsers());
        model.addAttribute("countNotices", adminService.countNotices());
        return "admin/admin";
    }

    @GetMapping("/user")
    public String user(Model model) {
        model.addAttribute("users", adminService.getAllUsers());
        model.addAttribute("countActiveUsers", adminService.countActiveUsers());
        model.addAttribute("countDeletedUsers", adminService.countDeletedUsers());
        model.addAttribute("countTodayUsers", adminService.countTodayUsers());

        return "admin/user";
    }

    @GetMapping("/user/{no}")
    public String userDetail(@PathVariable int no, Model model) {
        model.addAttribute("user", adminService.getUserByIdWithRoleNames(no));
        return "admin/user-info";
    }

    @PostMapping("/user/softDelete/{no}")
    public String softDelete(@PathVariable int no) {
        adminService.softDeleteUserById(no);

        return "redirect:/admin/user";
    }

    @PostMapping("/user/softRestore/{no}")
    public String softRestore(@PathVariable int no) {
        adminService.softRestoreUserById(no);

        return "redirect:/admin/user";
    }

    @GetMapping("/video")
    public String video() {
        return "admin/video";
    }

    @GetMapping("/video/{no}")
    public String videoPlay(@PathVariable int no) {
        return "admin/video-play";
    }

    @GetMapping("/product")
    public String product() {
        return "admin/product";
    }

    @GetMapping("/notice")
    public String notice(Model model) {
        model.addAttribute("notices", adminService.getAllNotices());
        return "admin/notice";
    }

    @GetMapping("/notice/detail/{no}")
    public String noticeDetail(@PathVariable int no, Model model) {
        model.addAttribute("notice", adminService.getNoticeById(no));
        return "admin/notice-detail";
    }

    @GetMapping("/notice/create")
    public String createNotice() {
        return "admin/notice-form";
    }

    @PostMapping("/notice/create")
    public String createNotice(@ModelAttribute AdminNoticeForm form) {

        // 테스트 아이디 고정
        User user = new User();
        user.setUserId(2);

        adminService.insertNotice(form, user.getUserId());

        return "redirect:/admin/notice";
    }

    @GetMapping("/notice/delete/{no}")
    public String deleteNotice(@PathVariable int no) {
        adminService.deleteNoticeById(no);
        return "redirect:/admin/notice";
    }

    @GetMapping("/notice/modify/{no}")
    public String modifyNotice(@PathVariable int no, Model model) {
        Notice notice = adminService.getNoticeById(no);
        model.addAttribute("notice", notice);
        return "admin/notice-modify-form";
    }

    @PostMapping("/notice/modify/{no}")
    public String modifyNotice(@PathVariable int no, @ModelAttribute AdminNoticeForm form) {
        form.setNoticeId(no);

        // 테스트 아이디 고정
        User user = new User();
        user.setUserId(2);

        System.out.println(form.toString());

        adminService.modifyNotice(form, user.getUserId());

        return "redirect:/admin/notice";
    }

}
