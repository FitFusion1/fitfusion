package com.fitfusion.web.controller;

import com.fitfusion.dto.MealRecordDto;
import com.fitfusion.security.SecurityUser;
import com.fitfusion.service.MealRecordService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/meal/daily-log")
public class MealDailyLogController {

    private final MealRecordService mealRecordService;

    /**
     *  식단 기록 페이지 조회
     */
    @GetMapping
    public String showDailyLogPage(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
                                   HttpSession session,
                                   Model model) {

        int userId = (int) session.getAttribute("userId");
        String recordDate = formatDate(date != null ? date : new Date());

        List<MealRecordDto> mealRecords = mealRecordService.getMealRecordsByDate(userId, recordDate);
        Map<String, Object> nutrientSummary = mealRecordService.calculateSummary(userId, recordDate);

        model.addAttribute("mealRecords", mealRecords);
        model.addAttribute("nutrientSummary", nutrientSummary);
        model.addAttribute("date", recordDate);

        return "meal/daily-log";
    }

    /**
     *  단건 저장 (음식 추가)
     */
    @PostMapping("/add")
    public String addMealRecord(@ModelAttribute MealRecordDto mealRecordDto,
                                @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
                                HttpSession session) {
        int userId = (int) session.getAttribute("userId");
        mealRecordDto.setUserId(userId);
        mealRecordDto.setRecordDate(date);

        mealRecordService.addMealRecord(mealRecordDto);

        return "redirect:/meal/daily-log?date=" + formatDate(date);
    }

    /**
     *  섭취량 수정
     */
    @PostMapping("/update/{id}")
    public String updateIntake(@PathVariable("id") int recordId,
                               @RequestParam double intake,
                               @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        mealRecordService.updateIntake(recordId, intake);
        return "redirect:/meal/daily-log?date=" + formatDate(date);
    }

//    /**
//     *  삭제
//     */
//    @PostMapping("/delete/{mealRecordId}")
//    public String deleteRecord(@PathVariable("mealRecordId") int mealRecordId,
//                               @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
//                               @AuthenticationPrincipal SecurityUser securityUser) {
//        int userId = securityUser.getUser().getUserId();
//        mealRecordService.deleteMealRecord(mealRecordId, userId);
//        return "redirect:/meal/daily-log?date=" + formatDate(date);
//    }

    /**
     *  공통 날짜 포맷 메서드
     */
    private String formatDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }
}
