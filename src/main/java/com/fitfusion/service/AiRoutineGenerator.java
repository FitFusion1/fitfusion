package com.fitfusion.service;

import com.fitfusion.enums.BodyPart;
import com.fitfusion.vo.Exercise;
import com.fitfusion.vo.RecommendedExercise;
import com.fitfusion.web.form.ExerciseConditionForm;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiRoutineGenerator {

    @Value("${openai.api.key}")
    private String apiKey;

    private final OkHttpClient httpClient = new OkHttpClient();

    public List<RecommendedExercise> generateRoutine(ExerciseConditionForm condition, List<Exercise> allExercises, String goalType) throws Exception {

        // ✅ 문자열 → BodyPart enum 변환
        List<BodyPart> avoidParts = condition.getAvoidParts().stream()
                .map(this::stringToBodyPart)
                .filter(Objects::nonNull)
                .toList();

        List<BodyPart> targetParts = condition.getTargetParts().stream()
                .map(this::stringToBodyPart)
                .filter(Objects::nonNull)
                .toList();

        // ✅ category 기반 필터링
        List<Exercise> filteredExercises = allExercises.stream()
                .filter(e -> {
                    String category = e.getCategory();
                    if (category == null) return false;

                    // 피해야 할 부위 제외
                    for (BodyPart avoid : avoidParts) {
                        if (category.equals(avoid.getBodyName())) return false;
                    }

                    // 하고 싶은 부위가 있다면 해당 부위만 포함
                    if (!targetParts.isEmpty()) {
                        for (BodyPart target : targetParts) {
                            if (category.equals(target.getBodyName())) return true;
                        }
                        return false;
                    }

                    return true;
                })
                .toList();

        // 디버깅 로그
        System.out.println("✅ 필터링된 운동 목록 ID:");
        filteredExercises.forEach(e -> System.out.print(e.getId() + " "));
        System.out.println("\n");

        String prompt = buildPrompt(condition, filteredExercises, goalType);
        String response = callOpenAiApi(prompt);

        System.out.println("⛳ OpenAI 응답 원문:\n" + response);

        return parseResponse(response, filteredExercises);
    }

    private String buildPrompt(ExerciseConditionForm condition, List<Exercise> filteredExercises, String goalType) {
        StringBuilder sb = new StringBuilder();

        sb.append("사용자의 운동 목표는 ").append(goalType).append("입니다.\n");
        sb.append("다음은 사용자의 운동 조건입니다:\n");
        sb.append("- 현재 컨디션: ").append(condition.getConditionLevel()).append("\n");
        sb.append("- 피해야 할 부위: ").append(String.join(", ", condition.getAvoidParts())).append("\n");
        sb.append("- 운동하고 싶은 부위: ").append(String.join(", ", condition.getTargetParts())).append("\n");

        sb.append("\n사용 가능한 운동 목록은 다음과 같습니다:\n");
        for (Exercise ex : filteredExercises) {
            sb.append("- [ID:").append(ex.getId()).append("] ").append(ex.getName()).append(": ").append(ex.getDescription()).append("\n");
        }

        sb.append("\n위 목록 중에서만 운동을 선택하여 총 5개의 운동 루틴을 추천해주세요.\n");
        sb.append("절대 존재하지 않는 ID나 목록에 없는 운동을 만들지 마세요.\n");
        sb.append("응답은 다음 JSON 형식으로 반환해주세요:\n");
        sb.append("[\n");
        sb.append("  {\"exerciseId\": 1, \"sets\": 3, \"reps\": 12},\n");
        sb.append("  {\"exerciseId\": 2, \"sets\": 4, \"reps\": 10}\n");
        sb.append("]");

        return sb.toString();
    }

    private String callOpenAiApi(String prompt) throws Exception {
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");

        JSONObject message = new JSONObject()
                .put("role", "user")
                .put("content", prompt);

        JSONObject payload = new JSONObject()
                .put("model", "gpt-3.5-turbo")
                .put("messages", new JSONArray().put(message))
                .put("temperature", 0.7);

        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .post(RequestBody.create(payload.toString(), mediaType))
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new Exception("OpenAI API 요청 실패: " + response.code() + " - " + response.message());
            }
            return response.body().string();
        }
    }

    private List<RecommendedExercise> parseResponse(String response, List<Exercise> filteredExercises) {
        List<RecommendedExercise> list = new ArrayList<>();

        try {
            JSONObject json = new JSONObject(response);

            if (json.has("error")) {
                System.err.println("OpenAI 오류: " + json.getJSONObject("error").getString("message"));
                return list;
            }

            if (!json.has("choices")) {
                System.err.println("OpenAI 응답에 'choices' 키가 없습니다.");
                return list;
            }

            String content = json
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
                    .trim();

            System.out.println("📦 OpenAI 응답 내용:\n" + content);

            JSONArray array = new JSONArray(content);

            Set<Integer> validIds = filteredExercises.stream()
                    .map(Exercise::getId)
                    .collect(Collectors.toSet());

            System.out.println("🎯 AI 추천된 ID와 필터링된 목록 비교:");
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                int id = obj.getInt("exerciseId");
                System.out.println(" - ID: " + id + " → " + (validIds.contains(id) ? "✅ 포함됨" : "❌ 없음"));
            }

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                int exerciseId = obj.getInt("exerciseId");

                Exercise matched = filteredExercises.stream()
                        .filter(e -> e.getId() == exerciseId)
                        .findFirst()
                        .orElse(null);

                if (matched == null) {
                    System.err.println("❗ OpenAI가 추천한 ID " + exerciseId + " 는 조건 필터에 의해 제거된 운동입니다 → 제외됨");
                    continue;
                }

                RecommendedExercise re = new RecommendedExercise();
                re.setExerciseId(exerciseId);
                re.setSets(obj.getInt("sets"));
                re.setReps(obj.getInt("reps"));
                re.setWeight(obj.has("weight") ? obj.getInt("weight") : 0);
                re.setExercise(matched);

                list.add(re);
            }

        } catch (Exception e) {
            System.err.println("AI 응답 파싱 중 오류 발생:");
            e.printStackTrace();
        }

        return list;
    }

    // ✅ 문자열 → BodyPart enum 변환 유틸
    private BodyPart stringToBodyPart(String name) {
        for (BodyPart bp : BodyPart.values()) {
            if (bp.name().equalsIgnoreCase(name.trim()) || bp.getBodyName().equalsIgnoreCase(name.trim())) {
                return bp;
            }
        }
        return null;
    }
}
