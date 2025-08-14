//package com.fitfusion.web.controller;
//
//import com.fitfusion.dto.MealRecordDto;
//import com.fitfusion.dto.NutrientSummaryDto;
//import com.fitfusion.enums.MealGoalType;
//import com.fitfusion.enums.MealTime; // enum.MealTime
//import com.fitfusion.security.SecurityUser;
//import com.fitfusion.service.MealEvaluationService;
//import com.fitfusion.service.MealRecordService;
////import com.fitfusion.dto.EvaluationResultDto;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//
//@Controller
//@RequiredArgsConstructor
//public class MealEvaluationController {
//
//    private final MealEvaluationService mealEvaluationService;
//    private final MealRecordService mealRecordService;
//
//    // 날짜 포맷터는 상수로 선언하여 재사용합니다.
//    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
//
//    @GetMapping("/meal/evaluate")
//    public String evaluateToday(@AuthenticationPrincipal SecurityUser securityUser,
//                                @RequestParam(required = false) MealGoalType goalType,
//                                @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
//                                Model model) {
////
////        if (securityUser == null || securityUser.getUser() == null) {
////            throw new IllegalArgumentException("로그인이 필요합니다.");
////        }
////        int userId = securityUser.getUser().getUserId();
////        User currentUser = securityUser.getUser();
////
////        // **** goalType이 null일 경우 기본값을 설정하고 이 변수 (actualGoalType)를 사용합니다. ****
////        DietGoalType actualGoalType = (goalType != null) ? goalType : DietGoalType.MAINTAIN; // 기본값을 MAINTAIN으로 설정
////
////        // 날짜 계산 로직: String으로 Model에 추가합니다.
////        Date targetDate = (date == null) ? new Date() : date;
////        String currentDateString = DATE_FORMAT.format(targetDate); // HTML에 보여줄 String 형태의 현재 날짜
////
////        // 이전/다음 날짜 String 계산
////        Calendar cal = Calendar.getInstance();
////        cal.setTime(targetDate);
////        cal.add(Calendar.DATE, -1);
////        String prevDateString = DATE_FORMAT.format(cal.getTime());
////        cal.add(Calendar.DATE, 2); // -1에서 +1로 이동하기 위해 +2
////        String nextDateString = DATE_FORMAT.format(cal.getTime());
////
////
////        // **** Model에 뷰에 필요한 데이터들을 추가합니다. ****
////        model.addAttribute("date", currentDateString); // 현재 날짜 (String)
////        model.addAttribute("prevDate", prevDateString); // 이전 날짜 (String)
////        model.addAttribute("nextDate", nextDateString); // 다음 날짜 (String)
////        model.addAttribute("goalType", actualGoalType); // 실제 사용될 goalType (Enum)
////
////        // 1. MealRecordService에서 식단 기록 정보와 영양소 요약을 가져옵니다.
////        // 서비스 메서드 호출 시에는 String이 아닌 Date 객체 'targetDate'를 사용합니다.
////        Map<String, Object> todaySummary = mealRecordService.getTodayNutrientSummary(userId, targetDate);
////        Map<MealTime, List<MealRecordDto>> mealRecords = mealRecordService.getTodayMealRecordsGroupedByMealTime(userId, targetDate);
////
////        // Map에서 DTO로 변환하여 사용 (getDoubleValue 헬퍼 함수 활용)
////        NutrientSummaryDto summaryDto = NutrientSummaryDto.builder()
////                .calories(getDoubleValue(todaySummary, "calories"))
////                .carbohydrateG(getDoubleValue(todaySummary, "carbohydrateG"))
////                .proteinG(getDoubleValue(todaySummary, "proteinG"))
////                .fatG(getDoubleValue(todaySummary, "fatG"))
////                // 오늘 영양소 합계 Map에 다른 필드가 있다면 여기에 추가:
////                .sugarG(getDoubleValue(todaySummary, "sugarG"))
////                .fiberG(getDoubleValue(todaySummary, "fiberG"))
////                .sodiumMg(getDoubleValue(todaySummary, "sodiumMg"))
////                .cholesterolMg(getDoubleValue(todaySummary, "cholesterolMg"))
////                .saturatedFatG(getDoubleValue(todaySummary, "saturatedFatG"))
////                .transFatG(getDoubleValue(todaySummary, "transFatG"))
////                .build();
////
////        // 2. MealEvaluationService에 평가 로직을 위임하고, 평가 결과를 EvaluationResultDto로 받아옵니다.
////        // 서비스 호출 시 actualGoalType을 전달합니다.
////        EvaluationResultDto evaluationResult = mealEvaluationService.evaluateMealNutrients(currentUser, actualGoalType, summaryDto);
////
////        // 3. 모델에 필요한 평가 결과 데이터들을 추가합니다.
////        model.addAttribute("goalCalories", evaluationResult.getGoalCalories());
////        model.addAttribute("todaySummary", todaySummary); // 영양소 요약은 Map<String, Object> 형태로 전달
////        model.addAttribute("score", evaluationResult.getScore());
////
////        model.addAttribute("mealRecords", mealRecords); // 식단 기록 (Map<MealTime, List<MealRecordDto>>)
////        model.addAttribute("carbPercent", evaluationResult.getCarbPercent());
////        model.addAttribute("proteinPercent", evaluationResult.getProteinPercent());
////        model.addAttribute("fatPercent", evaluationResult.getFatPercent());
////
//        return "meal-evaluation/meal-daily-nutrient";
//    }
////
////    // Map<String, Object>에서 값을 Double로 안전하게 변환하는 헬퍼 메소드
////    private double getDoubleValue(Map<String, Object> map, String key) {
////        Object value = map.get(key);
////        if (value instanceof Number) {
////            return ((Number) value).doubleValue();
////        }
////        return 0.0;
////    }
//}