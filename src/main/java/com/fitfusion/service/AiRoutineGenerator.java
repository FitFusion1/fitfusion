package com.fitfusion.service;

import com.fitfusion.enums.BodyPart;
import com.fitfusion.enums.ConditionLevel;
import com.fitfusion.enums.GoalType;
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

    public List<RecommendedExercise> generateRoutine(ExerciseConditionForm condition, List<Exercise> allExercises, String goalTypeName) throws Exception {
        System.out.println("🎯 전달된 사용자 목표(goalTypeName): " + goalTypeName);
        if (goalTypeName == null) {
            throw new IllegalStateException("❗ 사용자 운동 목표(goalTypeName)가 null입니다. 목표를 먼저 설정해야 합니다.");
        }

        GoalType goalType;
        try {
            goalType = GoalType.fromGoalName(goalTypeName);
            System.out.println("✅ GoalType enum 매핑 결과: " + goalType);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("❗ 잘못된 goalType 값입니다: " + goalTypeName);
        }

        ConditionLevel conditionLevel;
        try {
            conditionLevel = ConditionLevel.fromLevel(condition.getConditionLevel());
            System.out.println("✅ ConditionLevel 매핑 결과: " + conditionLevel);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("❗ 잘못된 conditionLevel 값입니다: " + condition.getConditionLevel());
        }

        List<BodyPart> avoidParts = condition.getAvoidParts().stream()
                .map(this::stringToBodyPart)
                .filter(Objects::nonNull)
                .toList();

        List<BodyPart> targetParts = condition.getTargetParts().stream()
                .map(this::stringToBodyPart)
                .filter(Objects::nonNull)
                .toList();

        List<Exercise> partFiltered = allExercises.stream()
                .filter(e -> {
                    String category = e.getCategory();
                    if (category == null) return false;

                    for (BodyPart avoid : avoidParts) {
                        if (category.equalsIgnoreCase(avoid.getBodyName())) return false;
                    }

                    if (!targetParts.isEmpty()) {
                        for (BodyPart target : targetParts) {
                            if (category.equalsIgnoreCase(target.getBodyName())) return true;
                        }
                        return false;
                    }

                    return true;
                })
                .toList();

        List<Exercise> filteredExercises = applyGoalSpecificFilter(partFiltered, goalType, conditionLevel);

        Set<Integer> selectedExerciseIds = new HashSet<>();
        List<Exercise> ensuredTargetExercises = new ArrayList<>();

        for (BodyPart target : targetParts) {
            partFiltered.stream()
                    .filter(e -> target.getBodyName().equalsIgnoreCase(e.getCategory()))
                    .filter(e -> !selectedExerciseIds.contains(e.getId()))
                    .findFirst()
                    .ifPresent(e -> {
                        ensuredTargetExercises.add(e);
                        selectedExerciseIds.add(e.getId());
                    });
        }

        List<Exercise> finalExercises = new ArrayList<>(ensuredTargetExercises);
        for (Exercise e : filteredExercises) {
            if (finalExercises.size() >= 5) break;
            if (!selectedExerciseIds.contains(e.getId())) {
                finalExercises.add(e);
                selectedExerciseIds.add(e.getId());
            }
        }

        System.out.println("🎯 필터링 전 운동 수: " + allExercises.size());
        System.out.println("🎯 파트 기준 필터링 후 수: " + partFiltered.size());
        System.out.println("🎯 목표 기준 최종 필터링 후 수: " + finalExercises.size());

        if (finalExercises.isEmpty()) {
            throw new IllegalStateException("❗ 조건에 맞는 운동이 없습니다. 필터 조건을 완화하거나 운동 목록을 보완하세요.");
        }

        String prompt = buildPrompt(condition, finalExercises, goalType);
        String response = callOpenAiApi(prompt);

        System.out.println("⛳ OpenAI 응답 원문:\n" + response);

        return parseResponse(response, finalExercises);
    }

    private List<Exercise> applyGoalSpecificFilter(List<Exercise> exercises, GoalType goalType, ConditionLevel conditionLevel) {
        return exercises.stream()
                .filter(e -> {
                    String category = e.getCategory();
                    int fatigue = e.getFatigueLevel();
                    if (category == null) return false;

                    return switch (goalType) {
                        case LOSS_WEIGHT -> (
                                (category.equalsIgnoreCase(BodyPart.AEROBIC.getBodyName()) || category.equalsIgnoreCase(BodyPart.LEGS.getBodyName()))
                                        && fatigue >= 3
                        );
                        case GAIN_WEIGHT -> (
                                fatigue >= 3 &&
                                        List.of(BodyPart.CHEST, BodyPart.BACK, BodyPart.LEGS).stream()
                                                .map(BodyPart::getBodyName)
                                                .toList().contains(category)
                        );
                        case MAINTAIN_WEIGHT -> fatigue <= 3;
                        case GAIN_MUSCLE -> (
                                fatigue >= 3 &&
                                        List.of(BodyPart.CHEST, BodyPart.BACK, BodyPart.SHOULDERS, BodyPart.LEGS, BodyPart.ARMS).stream()
                                                .map(BodyPart::getBodyName)
                                                .toList().contains(category)
                                        && isAllowedByCondition(fatigue, conditionLevel)
                        );
                        case IMPROVE_HEALTH -> (
                                fatigue <= 3 &&
                                        List.of(BodyPart.CORE, BodyPart.SHOULDERS, BodyPart.AEROBIC, BodyPart.LEGS).stream()
                                                .map(BodyPart::getBodyName)
                                                .toList().contains(category)
                        );
                    };
                })
                .toList();
    }

    private boolean isAllowedByCondition(int fatigue, ConditionLevel conditionLevel) {
        return switch (conditionLevel) {
            case GOOD -> true;
            case NORMAL -> fatigue <= 3;
            case BAD -> fatigue <= 2;
        };
    }

    private String buildPrompt(ExerciseConditionForm condition, List<Exercise> filteredExercises, GoalType goalType) {
        StringBuilder sb = new StringBuilder();
        sb.append("사용자의 운동 목표는 ").append(goalType.getGoalName()).append("입니다.\n");
        sb.append("다음은 사용자의 운동 조건입니다:\n");
        sb.append("- 현재 컨디션: ").append(condition.getConditionLevel()).append("\n");
        sb.append("- 피해야 할 부위: ").append(String.join(", ", condition.getAvoidParts())).append("\n");
        sb.append("- 운동하고 싶은 부위: ").append(String.join(", ", condition.getTargetParts())).append("\n");

        sb.append("\n사용 가능한 운동 목록은 다음과 같습니다:\n");
        for (Exercise ex : filteredExercises) {
            sb.append("- [ID:").append(ex.getId()).append("] ").append(ex.getName()).append(": ").append(ex.getDescription()).append("\n");
        }

        sb.append("\n운동 루틴 구성 기준은 다음과 같습니다:\n");
        switch (goalType) {
            case LOSS_WEIGHT -> sb.append(" - 유산소 및 근지구력 중심, 세트당 15~20회, 3~4세트\n");
            case GAIN_WEIGHT -> sb.append(" - 고중량 근력 중심, 세트당 6~10회, 4~5세트\n");
            case MAINTAIN_WEIGHT -> sb.append(" - 전신운동 위주, 10~15회, 2~3세트\n");
            case GAIN_MUSCLE -> sb.append(" - 근육 부위 분할 고강도 운동, 8~12회, 4~6세트\n");
            case IMPROVE_HEALTH -> sb.append(" - 스트레칭 및 가벼운 유산소, 반복 수 자유, 2~3세트\n");
        }

        sb.append("\n위 목록 중에서만 운동을 선택하여 총 5개의 운동 루틴을 추천해주세요.\n")
                .append("절대 존재하지 않는 ID나 목록에 없는 운동을 만들지 마세요.\n")
                .append("응답은 다음 JSON 형식으로 반환해주세요:\n")
                .append("[\n")
                .append("  {\"exerciseId\": 1, \"sets\": 3, \"reps\": 15},\n")
                .append("  {\"exerciseId\": 2, \"sets\": 4, \"reps\": 12}\n")
                .append("]");

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
                    System.err.println("❗ OpenAI가 추천한 ID " + exerciseId + " 는 필터링 목록에 없음 → 제외됨");
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

    private BodyPart stringToBodyPart(String name) {
        for (BodyPart bp : BodyPart.values()) {
            if (bp.name().equalsIgnoreCase(name.trim()) || bp.getBodyName().equalsIgnoreCase(name.trim())) {
                return bp;
            }
        }
        return null;
    }
}
