package com.fitfusion.web.controller;

import com.fitfusion.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping
    public String admin(Model model) {
        model.addAttribute("countActiveUsers", adminService.countActiveUsers());

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

    @GetMapping("/notice")
    public String notice() {
        return "admin/notice";
    }

    @GetMapping("/product")
    public String product() {
        return "admin/product";
    }

}
