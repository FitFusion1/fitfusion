package com.fitfusion.web.controller;

import com.fitfusion.dto.FoodDto;
import com.fitfusion.dto.MealRecordDto;
import com.fitfusion.dto.MealRecordWrapper;
import com.fitfusion.dto.PageResponseDto;
import com.fitfusion.enums.MealTime;
import com.fitfusion.security.SecurityUser;
import com.fitfusion.service.MealRecordService;
import com.fitfusion.service.FoodService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.beans.PropertyEditorSupport;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/meal")
public class MealRecordController {

    private final MealRecordService mealRecordService;
    private final FoodService foodService;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    // MealTime enum 바인딩 오류 방지용
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(MealTime.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue((text == null || text.isBlank()) ? null : MealTime.valueOf(text));
            }
        });
    }

    /**
     * 식단 기록 페이지 (검색 + 페이징)
     * - 검색어(keyword), 페이지(pageNum) 파라미터 기반으로 음식 검색
     * - 날짜와 끼니(mealTime)는 항상 유지 (날짜와 끼니에 맞는 음식들을 조회)
     */
    @GetMapping("/record")
    public String showMealRecordPage(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) MealTime mealTime,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal SecurityUser securityUser,
            Model model) {

        int userId = securityUser.getUser().getUserId();

        // 선택된 날짜가 없으면 오늘
        Date baseDate = (date != null) ? date : new Date();

        // 이전/다음 날짜 계산
        Calendar cal = Calendar.getInstance();
        cal.setTime(baseDate);
        cal.add(Calendar.DATE, -1);
        Date prevDate = cal.getTime();
        cal.add(Calendar.DATE, 2);
        Date nextDate = cal.getTime();

        // 끼니 선택, 기본값: BREAKFAST
        MealTime viewMealTime = (mealTime != null ? mealTime : MealTime.BREAKFAST);

        // DB 조회 (Date 타입 그대로)
        Map<MealTime, List<MealRecordDto>> groupedRecords =
                Optional.ofNullable(mealRecordService.getMealRecordsGroupedByMealTime(userId, baseDate))
                        .orElse(Collections.emptyMap());

        model.addAttribute("mealRecords", groupedRecords);
        model.addAttribute("date", DATE_FORMAT.format(baseDate));
        model.addAttribute("prevDate", DATE_FORMAT.format(prevDate));
        model.addAttribute("nextDate", DATE_FORMAT.format(nextDate));
        model.addAttribute("mealTimes", MealTime.values());
        model.addAttribute("size", size);

        // MealRecordWrapper + 검색 + 페이징 처리
        MealRecordWrapper wrapper = new MealRecordWrapper();
        List<FoodDto> foods = null;

        if (keyword != null && !keyword.isBlank()) {
            PageResponseDto<FoodDto> pageResult = foodService.searchFoodsPaged(keyword, page, size);
            foods = pageResult.getContent();

            model.addAttribute("foods", foods);
            model.addAttribute("pageResult", pageResult);

            if (foods != null && !foods.isEmpty()) {
                for (FoodDto food : foods) {
                    MealRecordDto record = new MealRecordDto();
                    record.setMealTime(viewMealTime);
                    record.setRecordDate(baseDate);
                    record.setFoodServingSizeValue(food.getFoodServingSizeValue());
                    record.setFoodServingSizeUnit(food.getFoodServingSizeUnit());
                    wrapper.getMealRecords().add(record);
                }
            }
        }

        model.addAttribute("selectedMealTime", viewMealTime);
        model.addAttribute("mealRecordWrapper", wrapper);
        model.addAttribute("keyword", keyword != null ? keyword : "");

        return "meal-record/meal-record";
    }

    @PostMapping("/save")
    public String saveMealRecords(@ModelAttribute("wrapper") MealRecordWrapper wrapper,
                                  @AuthenticationPrincipal SecurityUser securityUser,
                                  @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
                                  RedirectAttributes redirectAttributes) {

        // 1. 로그인한 사용자 ID 확인
        if (securityUser == null || securityUser.getUser() == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
            return "redirect:/meal/record";
        }
        final int userId = securityUser.getUser().getUserId();

        // 2. MealRecordWrapper 디버깅
        if (wrapper == null || wrapper.getMealRecords() == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "요청 데이터가 잘못되었습니다.");
            return "redirect:/meal/record" + (date != null ? "?date=" + DATE_FORMAT.format(date) : "");
        }

        // 3. 체크된 음식만 필터링
        List<MealRecordDto> validRecords = wrapper.getMealRecords().stream()
                .filter(r -> r.getFoodId() != null)
                .peek(r -> {
                    r.setUserId(userId);               // 로그인 사용자 ID 세팅
                    r.setCreatedDate(null);            // DB default
                    r.setUpdatedDate(null);

                    if (r.getMealTime() == null) {
                        r.setMealTime(MealTime.BREAKFAST);
                    }

                    if (r.getUserIntake() == null || r.getUserIntake() <= 0) {
                        r.setUserIntake(r.getFoodServingSizeValue());
                    }
                })
                .toList();

        // 4. 유효한 데이터가 없으면 → 에러 메시지 + 기존 페이지로 반환
        if (validRecords.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "선택된 음식이 없습니다.");
            return "redirect:/meal/record" + (date != null ? "?date=" + DATE_FORMAT.format(date) : "");
        }

        // 5. DB 저장
        mealRecordService.saveMealRecords(validRecords);

        // 6. 저장 후 날짜 페이지로 리다이렉트
        Date baseDate = validRecords.stream()
                .map(MealRecordDto::getRecordDate)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(new Date());

        String redirectDate = DATE_FORMAT.format(validRecords.get(0).getRecordDate());

        return "redirect:/meal/record?date=" + redirectDate;
    }

    @PostMapping("/delete/{mealRecordId}")
    public String deleteMealRecord(@PathVariable("mealRecordId") int mealRecordId,
                                   HttpServletRequest request,
                                   @AuthenticationPrincipal SecurityUser securityUser) {
        // 1. 로그인 사용자 확인 및 예외 처리
        if (securityUser == null || securityUser.getUser() == null) {
            log.warn("식단 기록 삭제 실패: 비로그인 사용자 요청");

            return "redirect:/meal/record";
        }

        final int userId = securityUser.getUser().getUserId();

        // 2. 서비스 호출 및 예외 처리
        try {
            mealRecordService.deleteMealRecord(mealRecordId, userId); // 핵심 로직 호출
            log.info(" [SUCCESS] 식단 기록 삭제 성공: mealRecordId = {}", mealRecordId);

        } catch (AccessDeniedException e) { // '삭제 권한이 없습니다.' 예외 처리
            log.warn("[WARNING] 삭제 권한 없음 (mealRecordId: {} / userId: {}): {}", mealRecordId, userId, e.getMessage());

        } catch (IllegalArgumentException e) { // '삭제할 식단 기록이 없습니다.' 예외 처리
            log.warn("[WARNING] 삭제할 기록 없음 (mealRecordId: {}): {}", mealRecordId, e.getMessage());

        } catch (Exception e) { // 그 외 예상치 못한 서버 오류
            log.error("[ERROR] 식단 기록 삭제 중 예상치 못한 오류 발생 (mealRecordId: {} / userId: {}): {}", mealRecordId, userId, e.getMessage(), e);
        }

        // 3. Referer를 사용하여 이전 페이지로 리다이렉트
        String referer = request.getHeader("Referer");
        if (referer == null || referer.isBlank()) {
            return "redirect:/meal/record"; // Referer가 없는 경우 기본 리다이렉트
        }
        return "redirect:" + referer; // 이전 페이지로 리다이렉트
    }

    /**
     *  섭취량 수정
     */
    @PostMapping("/update/{mealRecordId}")
    public String updateIntake(
            @PathVariable("mealRecordId") Integer mealRecordId,
            @RequestParam("userIntake") int userIntake,
            HttpServletRequest request
    ) {
        // Referer를 사용하여 이전 페이지로 리다이렉트
        String referer = request.getHeader("Referer");
        System.out.println("Referer: " + referer); // 디버깅용

        // --- 여기에 섭취량(userIntake)을 데이터베이스에 업데이트하는 로직 추가 ---
        try {
            mealRecordService.updateIntake(mealRecordId, userIntake);
            System.out.println("섭취량 업데이트 성공: MealRecordId=" + mealRecordId);

        } catch (Exception e) {

            System.err.println("섭취량 업데이트 실패: MealRecordId=" + mealRecordId + ", 오류: " + e.getMessage());
        }

        if (referer == null || referer.isBlank()) {
            return "redirect:/meal/detail/" + mealRecordId;
        }
        return "redirect:" + referer; // Referer가 있는 경우 이전 페이지로 리다이렉트
    }

//    // 식단 기록 상세 조회 (선택한 단일 음식의 detail 뷰)
//    @GetMapping("/meal-record/{mealRecordId}")
//    public String getMealRecord(@PathVariable int mealRecordId, Model model) {
//        MealRecordDto record = mealRecordService.getMealRecordDetail(mealRecordId);
//        model.addAttribute("record", record);
//        return "meal-record-page";
//    }

}
