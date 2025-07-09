package com.fitfusion.web.controller;

import com.fitfusion.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping
    public String admin(Model model) {
        model.addAttribute("users", adminService.getAllUsers());

        return "admin/admin";
    }

    @GetMapping("/user")
    public String user(Model model) {
        model.addAttribute("users", adminService.getAllUsers());

        return "admin/user";
    }

    @GetMapping("/user/{no}")
    public String userDetail(@PathVariable String no, Model model) {

        model.addAttribute("user");
        return "admin/user-info";
    }

    @GetMapping("/video")
    public String videos() {
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
