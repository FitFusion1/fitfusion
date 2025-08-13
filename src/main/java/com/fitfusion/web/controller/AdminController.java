package com.fitfusion.web.controller;

import com.fitfusion.security.SecurityUser;
import com.fitfusion.service.AdminService;
import com.fitfusion.vo.Exercise;
import com.fitfusion.vo.Notice;
import com.fitfusion.vo.User;
import com.fitfusion.vo.Video;
import com.fitfusion.web.form.AdminNoticeForm;
import com.fitfusion.web.form.AdminVideoForm;
import com.fitfusion.web.view.FileDownloadView;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;


@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private FileDownloadView fileDownloadView;

    @GetMapping
    public String admin(Model model) {
        model.addAttribute("countActiveUsers", adminService.countActiveUsers());
        model.addAttribute("countNotices", adminService.countNotices());
        model.addAttribute("countVideos", adminService.countVideos());
        model.addAttribute("countExercises", adminService.countExercises());
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
    public String video(Model model) {
        model.addAttribute("videos", adminService.getAllVideos());

        return "admin/video";
    }

    @GetMapping("/video/{no}")
    public String videoPlay(@PathVariable int no, Model model) {
        model.addAttribute("video", adminService.getVideoById(no));
        return "admin/video-play";
    }


    @GetMapping("/video/delete/{no}")
    public String deleteVideo(@PathVariable int no) {
        adminService.deleteVideoById(no);
        return "redirect:/admin/video";
    }

    @GetMapping("/video/create")
    public String createVideo(Model model) {
        model.addAttribute("exercises", adminService.getAllExercises());
        model.addAttribute("categories", adminService.getAllVideoCategories());
        return "admin/video-form";
    }

    @PostMapping("/video/create")
    public String createVideo(@ModelAttribute AdminVideoForm form, @AuthenticationPrincipal SecurityUser securityUser) {
        User user = securityUser.getUser();
        adminService.insertVideo(form, user.getUserId());

        return "redirect:/admin/video";
    }

    @GetMapping("/video/modify/{id}")
    public String modifyVideo(@PathVariable("id") int id, Model model) {
        Video video = adminService.getVideoById(id);
        if (video == null) {
            throw new RuntimeException("해당 ID의 영상을 찾을 수 없습니다: " + id);
        }

        model.addAttribute("form", video);
        model.addAttribute("videoId", id);
        model.addAttribute("exercises", adminService.getAllExercises());
        model.addAttribute("categories", adminService.getAllVideoCategories());

        return "admin/video-modify-form";
    }

    @PostMapping("/video/modify/{id}")
    public String updateVideo(
            @PathVariable("id") int id,
            @ModelAttribute AdminVideoForm form,
            @AuthenticationPrincipal SecurityUser securityUser
    ) {
        User user = securityUser.getUser();

        // 비즈니스 로직은 서비스로 위임
        adminService.modifyVideo(form, user, id);

        return "redirect:/admin/video";
    }

    @GetMapping("/product")
    public String product() {
        return "admin/product";
    }

    @GetMapping("/exercise")
    public String exercise(Model model) {
        model.addAttribute("exercises", adminService.getAllExercises());
        model.addAttribute("countExercises", adminService.countExercises());
        return "admin/exercise";
    }

    @GetMapping("/exercise/create")
    public String createExercise() {
        return "admin/exercise-form";
    }

    @PostMapping("/exercise/create")
    public String createExercise(@ModelAttribute Exercise exercise) {
        adminService.insertExercise(exercise);
        return "redirect:/admin/exercise";
    }

    @GetMapping("/exercise/delete/{no}")
    public String deleteExercise(@PathVariable int no) {
        adminService.deleteExerciseById(no);
        return "redirect:/admin/exercise";
    }

    @GetMapping("/exercise/modify/{no}")
    public String modifyExercise(@PathVariable int no, Model model) {
        Exercise exercise = adminService.getExerciseById(no);
        model.addAttribute("exercise", exercise);
        return "admin/exercise-modify-form";
    }

    @PostMapping("/exercise/modify/{no}")
    public String modifyExercise(@PathVariable int no, @ModelAttribute Exercise exercise, @AuthenticationPrincipal SecurityUser securityUser) {
        adminService.modifyExercise(exercise);

        return "redirect:/admin/exercise";
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
    public String createNotice(@ModelAttribute AdminNoticeForm form, @AuthenticationPrincipal SecurityUser securityUser) {

        User user = securityUser.getUser();

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
    public String modifyNotice(@PathVariable int no, @ModelAttribute AdminNoticeForm form, @AuthenticationPrincipal SecurityUser securityUser) {
        form.setNoticeId(no);

        User user = securityUser.getUser();

        adminService.modifyNotice(form, user.getUserId());

        return "redirect:/admin/notice";
    }

    @GetMapping("/users/export")
    public void exportUsersToExcel(HttpServletResponse response) throws IOException {
        String filename = "users_" + LocalDate.now() + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(filename, StandardCharsets.UTF_8));

        List<User> users = adminService.getAllUsers();

        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Users");

            String[] headers = {"회원번호", "아이디", "이름", "이메일", "성별", "키(cm)", "몸무게(kg)", "생년월일", "가입일", "상태"};
            Row header = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                header.createCell(i).setCellValue(headers[i]);
            }

            int rowIdx = 1;
            for (User u : users) {
                Row row = sheet.createRow(rowIdx++);
                int c = 0;
                row.createCell(c++).setCellValue(u.getUserId());
                row.createCell(c++).setCellValue(u.getUsername() != null ? u.getUsername() : "");
                row.createCell(c++).setCellValue(u.getName() != null ? u.getName() : "");
                row.createCell(c++).setCellValue(u.getEmail() != null ? u.getEmail() : "");
                row.createCell(c++).setCellValue(u.getGender() != null ? u.getGender() : "");
                row.createCell(c++).setCellValue(u.getHeight() != 0 ? Integer.toString(u.getHeight()) : "");
                row.createCell(c++).setCellValue(u.getWeight() != 0 ? Integer.toString(u.getWeight()) : "");
                row.createCell(c++).setCellValue(u.getBirthDate() != null ? u.getBirthDate().toString() : "");
                row.createCell(c++).setCellValue(u.getCreatedDate() != null ? u.getCreatedDate().toString() : "");
                row.createCell(c++).setCellValue("Y".equals(u.getDeleted()) ? "삭제" : "정상");
            }

            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);
            wb.write(response.getOutputStream());
        }
    }

    @GetMapping("/statistics")
    public String viewStatistics(Model model) {
        model.addAttribute("countActiveUsers", adminService.getActiveUserCount());
        model.addAttribute("countDeletedUsers", adminService.getDeletedUserCount());
        model.addAttribute("countTodayUsers", adminService.getTodayUserCount());
        model.addAttribute("countVideos", adminService.getVideoCount());
        model.addAttribute("countExercises", adminService.getExerciseCount());
        model.addAttribute("countNotices", adminService.getNoticeCount());
        return "admin/statistics";
    }

}
