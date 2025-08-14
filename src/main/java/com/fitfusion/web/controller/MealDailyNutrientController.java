package com.fitfusion.web.controller;

// 필요한 import 문 추가
import com.fitfusion.dto.NutrientSummaryDto; // 이 DTO가 필요합니다.
import com.fitfusion.enums.MealGoalType;       // MealGoalType enum
import com.fitfusion.enums.MealTime;
import com.fitfusion.security.SecurityUser;
import com.fitfusion.service.MealEvaluationService; // MealEvaluationService
import com.fitfusion.vo.User;                  // User VO
import com.fitfusion.dto.MealRecordDto;       // MealRecordDto
import com.fitfusion.service.MealRecordService; // mealRecordService 주입 필요
import com.fitfusion.service.UserService;       // UserService 주입 필요 (사용자 정보 조회용)


import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

// static import for SimpleDateFormat for DATE_FORMAT (assuming it's a constant)
// import static com.fitfusion.util.DateUtil.DATE_FORMAT; // 예시 (실제 DateUtil 경로에 맞게)
// DateUtil이 없다면 직접 SimpleDateFormat 상수를 선언하거나, format 메소드에서 새로 생성

@Controller
@RequiredArgsConstructor // Lombok으로 final 필드 자동 주입
@RequestMapping("/meal") // 기존 경로와 맞춤 (MealDailyNutrientController가 아니라 MealController 등으로 가정)
// 만약 이 컨트롤러가 /meal/evaluation 이었다면 위 RequestMapping을 /meal/evaluation으로 변경
public class MealDailyNutrientController { // 클래스 이름은 실제 컨트롤러에 맞게 수정해주세요 (예: MealController)

    private final MealRecordService mealRecordService; // 기존에 주입받던 서비스
    private final MealEvaluationService mealEvaluationService; // 새로 주입받을 서비스
    private final UserService userService; // User 정보를 가져오기 위한 서비스 (필수)

    // 날짜 포맷 상수 (만약 DateUtil에 정의되어 있지 않다면)
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 식단 기록 조회 및 식단 평가
     */
    @GetMapping("/evaluation") // 실제 HTML에서 이 URL로 호출하고 있다면 @GetMapping("/evaluation")
    // 아니라면 기존 @GetMapping 경로를 사용합니다.
    public String showDailyLogPage(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
            @RequestParam(required = false) MealTime mealTime,
            @RequestParam(required = false, name="continueParam") String continueParam,
            @AuthenticationPrincipal SecurityUser securityUser,
            Model model) {

        int userId = securityUser.getUser().getUserId();

        // ------------------ 기존 식단 기록 조회 및 영양소 요약 로직 ------------------
        Date currentDate = (date != null) ? date : new Date();

        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        cal.add(Calendar.DATE, -1);
        Date prevDate = cal.getTime();
        cal.add(Calendar.DATE, 2);
        Date nextDate = cal.getTime();

        MealTime viewMealTime = (mealTime != null ? mealTime : MealTime.BREAKFAST);

        // nutrientSummary는 Map<String, Object> 타입이므로, 이를 MealEvaluationService에
        // 넘겨주기 위한 NutrientSummaryDto로 변환해야 합니다.
        Map<String, Object> nutrientSummaryMap = // 이름 변경
                Optional.ofNullable(mealRecordService.calculateSummary(userId, currentDate))
                        .orElseGet(()-> {
                            Map<String, Object> emptyMap = new HashMap<>();
                            emptyMap.put("calories", 0.0); // double로 변경
                            emptyMap.put("carbohydrateG", 0.0);
                            emptyMap.put("fatG", 0.0);
                            emptyMap.put("proteinG", 0.0);
                            emptyMap.put("fiberG", 0.0);
                            emptyMap.put("sodiumMg", 0.0);
                            emptyMap.put("cholesterolMg", 0.0);
                            emptyMap.put("caloriesGoal", 0.0); // 목표 칼로리도 초기화
                            emptyMap.put("carbohydrateGGoal", 0.0);
                            emptyMap.put("fatGGoal", 0.0);
                            emptyMap.put("proteinGGoal", 0.0);
                            return emptyMap;
                        });

        // Map에서 NutrientSummaryDto로 변환 (MealEvaluationService에 전달용)
        NutrientSummaryDto actualNutrientSummary = new NutrientSummaryDto();
        // nutrientSummaryMap의 키가 HTML의 th:text와 동일한 대문자 형식이라면 다음과 같이 변환 (칼로리, 탄단지g)
        actualNutrientSummary.setCalories(((Number) nutrientSummaryMap.getOrDefault("CALORIES", 0.0)).doubleValue());
        actualNutrientSummary.setCarbohydrateG(((Number) nutrientSummaryMap.getOrDefault("CARBOHYDRATEG", 0.0)).doubleValue());
        actualNutrientSummary.setProteinG(((Number) nutrientSummaryMap.getOrDefault("PROTEING", 0.0)).doubleValue());
        actualNutrientSummary.setFatG(((Number) nutrientSummaryMap.getOrDefault("FATG", 0.0)).doubleValue());

        // 기존 끼니별 식단 기록 조회
        Map<MealTime, List<MealRecordDto>> groupedRecords =
                Optional.ofNullable(mealRecordService.getMealRecordsGroupedByMealTime(userId, currentDate))
                        .orElse(Collections.emptyMap());

        // 각 끼니별 칼로리 합계 계산
        Map<MealTime, String> mealTimeTotalCalories = new EnumMap<>(MealTime.class);
        for (MealTime time : MealTime.values()) {
            List<MealRecordDto> records = groupedRecords.getOrDefault(time, Collections.emptyList());
            double sumCalories = records.stream()
                    .filter(r -> r.getCalories() != null)
                    .mapToDouble(MealRecordDto::getCalories)
                    .sum();
            mealTimeTotalCalories.put(time, formatCaloriesDisplay(sumCalories)); // formatCaloriesDisplay 필요
        }

        // ------------- MealEvaluationService 호출 로직 추가 ---------------
        // 1. 사용자 정보 가져오기 (DB에서 User VO를 조회)
        // User currentUser = userService.getUserById(userId); // 실제 User 서비스 호출
        // TODO: 임시 User 객체 생성 (실제 DB에서 조회하는 코드로 대체 필요)
        User currentUser = new User();
        // 예시: 사용자 생년월일 설정 (실제 userService.getUserById(userId)에서 가져온 값 사용)
        try {
            currentUser.setBirthDate(new SimpleDateFormat("yyyy-MM-dd").parse("1994-03-26"));
        } catch (ParseException e) {
            e.printStackTrace();
            currentUser.setBirthDate(new Date());
        }
        currentUser.setWeight(70); // 실제 User 객체에서 가져옴
        currentUser.setHeight(170); // 실제 User 객체에서 가져옴
        currentUser.setGender("F"); // 실제 User 객체에서 가져옴


        // 2. 목표 설정 (사용자 설정에 따라 달라질 수 있음)
        // TODO: 만약 사용자가 목표를 선택한다면, 그 목표를 MealGoalType으로 변환하여 사용
        MealGoalType goalType = MealGoalType.MAINTAIN;


        // 3. MealEvaluationService를 사용하여 평가
        double goalCalories = mealEvaluationService.calculateGoalCalories(currentUser, goalType);
        int mealScore = mealEvaluationService.calculateMealScore(goalCalories, actualNutrientSummary);


        // 4. 평가 결과에 따른 피드백 메시지 생성 (이전과 동일)
        String mainMessage = "";
        String subMessage = "";
        String infoCard1Title = "";
        String infoCard1Content = "";
        // score-desc 에 사용될 문구
        String scoreDesc = "식단을 평가해보세요!";

        if (mealScore >= 90) {
            mainMessage = "🌟 완벽해요!";
            subMessage = "오늘도 잘 챙겨 드셨네요!";
            infoCard1Title = "💪 완벽한 식단 달성!";
            infoCard1Content = "최고의 균형으로 건강을 지키세요!";
            scoreDesc = "최고의 식단 밸런스!";
        } else if (mealScore >= 70) {
            mainMessage = "😊 잘했어요!";
            subMessage = "조금만 더 노력하면 완벽!";
            infoCard1Title = "👍 훌륭한 식단 관리!";
            infoCard1Content = "균형 잡힌 식단, 계속 이어나가세요!";
            scoreDesc = "꽤나 건강한 식단이에요!";
        } else if (mealScore >= 50) {
            mainMessage = "🤔 조금 아쉬워요";
            subMessage = "다음엔 더 잘할 수 있어요!";
            infoCard1Title = "⚠️ 영양 균형 점검!";
            infoCard1Content = "몇 가지 영양소를 더 신경 써 보세요.";
            scoreDesc = "조금만 더 신경써 볼까요?";
        } else {
            mainMessage = "🥲 치팅데이였나요?";
            subMessage = "맛있었으면 됐어요!";
            infoCard1Title = "🎉 눈감고 있을게요!";
            infoCard1Content = "하하, 오늘은 치팅데이🤭 오늘만 칼로리 리미트 해제인거 알죠?";
            scoreDesc = "맛있으면 0칼로리~";
        }


        // ------------------ Model에 데이터 추가 ------------------
        model.addAttribute("mealRecords", groupedRecords);
        model.addAttribute("mealTimeTotalCalories", mealTimeTotalCalories);
        model.addAttribute("nutrientSummary", nutrientSummaryMap); // 기존 Map 형태는 그대로 유지
        model.addAttribute("actualNutrientSummary", actualNutrientSummary); // MealEvaluationService 전달용 DTO (VIEW에서도 사용 가능)
        model.addAttribute("date", DATE_FORMAT.format(currentDate));
        model.addAttribute("prevDate", DATE_FORMAT.format(prevDate));
        model.addAttribute("nextDate", DATE_FORMAT.format(nextDate));
        model.addAttribute("mealTimes", MealTime.values());

        model.addAttribute("prevDateUrl", "/meal/evaluation?date=" + DATE_FORMAT.format(prevDate));
        model.addAttribute("nextDateUrl", "/meal/evaluation?date=" + DATE_FORMAT.format(nextDate));
        model.addAttribute("currentDateUrl", "/meal/evaluation?date=" + DATE_FORMAT.format(currentDate));

        // 식단 평가 관련 데이터
        model.addAttribute("goalCalories", goalCalories); // 계산된 목표 칼로리
        model.addAttribute("mealScore", mealScore);       // 최종 식단 점수
        model.addAttribute("mainMessage", mainMessage);   // 피드백 주 메시지
        model.addAttribute("subMessage", subMessage);     // 피드백 부 메시지
        model.addAttribute("infoCard1Title", infoCard1Title);
        model.addAttribute("infoCard1Content", infoCard1Content);
        model.addAttribute("scoreDesc", scoreDesc);       // 점수 설명 문구

        // TODO: 상세 영양소별 진행바 및 Top N 리스트 데이터 생성 로직 필요
        // List<NutrientDetailDto> nutrientDetails = buildNutrientDetails(...);
        // model.addAttribute("nutrientDetails", nutrientDetails);


        return "meal-evaluation/meal-daily-nutrient"; // 실제 뷰 경로와 일치
    }

    // TODO: formatCaloriesDisplay 메소드가 현재 컨트롤러에 없다면 추가해야 합니다.
    private String formatCaloriesDisplay(double calories) {
        // 소수점 없이 정수로 표시하거나, 특정 포맷에 맞춰 반환
        return String.valueOf((int)Math.round(calories));
    }
}