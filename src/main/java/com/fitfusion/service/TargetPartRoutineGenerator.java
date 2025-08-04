package com.fitfusion.service;

import com.fitfusion.enums.BodyPart;
import com.fitfusion.vo.Exercise;
import com.fitfusion.vo.RecommendedExercise;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TargetPartRoutineGenerator {

    @Value("${openai.api.key}")
    private String apiKey;


    private final OkHttpClient client = new OkHttpClient();

    public Map<BodyPart, List<RecommendedExercise>> generate(
            List<BodyPart> lackParts,
            List<Exercise> allExercises) throws Exception {


        Map<BodyPart, List<Exercise>> partExercises = lackParts.stream()
                .collect(Collectors.toMap(
                        bp -> bp,
                        bp -> allExercises.stream()
                                .filter(e -> bp.getBodyName()
                                        .equalsIgnoreCase(e.getCategory()))
                                .collect(Collectors.toList())
                ));


        String prompt = buildPrompt(partExercises);
        String raw    = callOpenAi(prompt);


        return parseResponse(raw, partExercises);
    }


    private String buildPrompt(Map<BodyPart, List<Exercise>> map) {
        StringBuilder sb = new StringBuilder();
        sb.append("당신은 전문 트레이너입니다.\n")
                .append("다음 부족 부위마다 **5가지 운동**으로 구성된 루틴을 1개씩 만들어 주세요.\n")
                .append("반드시 아래 후보 운동(ID 포함)만 사용하고 **순수 JSON 배열**만 반환하세요.\n")
                .append("형식 예시:\n")
                .append("[{\"bodyPart\":\"하체\",\"routine\":[{\"exerciseId\":1,\"sets\":3,\"reps\":12}, ...]}]\n\n")

                .append("⚠️ 주의사항\n")
                .append("1) 코드블럭(```), 설명문, 주석 모두 금지 — JSON 문자열만 출력\n")
                .append("2) 모든 routine 항목에 \"sets\"·\"reps\" 키 필수, 값은 **숫자만** (예: 4, 10)\n")
                .append("3) `weight`는 필요 시 숫자, 없으면 생략 가능\n\n");

        map.forEach((bp, list) -> {
            sb.append("### 부족 부위: ").append(bp.getBodyName()).append('\n');
            list.forEach(e -> sb.append("- [ID:")
                    .append(e.getExerciseId()).append("] ")
                    .append(e.getExerciseName()).append(": ")
                    .append(e.getDescription()).append('\n'));
            sb.append('\n');
        });
        return sb.toString();
    }


    private String callOpenAi(String prompt) throws Exception {
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");

        JSONObject payload = new JSONObject()
                .put("model", "gpt-3.5-turbo")
                .put("temperature", 1.0)
                .put("messages", new JSONArray()
                        .put(new JSONObject()
                                .put("role", "user")
                                .put("content", prompt)));

        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer " + apiKey) // ← 공백 주의!
                .post(RequestBody.create(payload.toString(), mediaType))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new Exception("OpenAI 호출 실패: " + response);
            }
            return response.body().string();
        }
    }

    private int safeInt(Object o) {
        if (o instanceof Number n) return n.intValue();
        if (o == null) return 0;
        String digits = o.toString().replaceAll("[^0-9]", "");
        return digits.isEmpty() ? 0 : Integer.parseInt(digits);
    }


    private Map<BodyPart, List<RecommendedExercise>> parseResponse(
            String raw,
            Map<BodyPart, List<Exercise>> candidates) {

        Map<BodyPart, List<RecommendedExercise>> result = new HashMap<>();


        Function<String, BodyPart> toBodyPart = korean ->
                Arrays.stream(BodyPart.values())
                        .filter(bp -> bp.getBodyName().equalsIgnoreCase(korean.trim()))
                        .findFirst()
                        .orElse(null);

        try {
            String content = new JSONObject(raw)
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
                    .trim();

            JSONArray routines = new JSONArray(content);

            for (int i = 0; i < routines.length(); i++) {
                JSONObject obj = routines.getJSONObject(i);
                BodyPart bp = toBodyPart.apply(obj.getString("bodyPart"));
                if (bp == null || !candidates.containsKey(bp)) continue;


                Set<Integer> validIds = candidates.get(bp).stream()
                        .map(Exercise::getExerciseId)
                        .collect(Collectors.toSet());

                List<RecommendedExercise> routineList = new ArrayList<>();
                JSONArray routine = obj.getJSONArray("routine");

                for (int j = 0; j < routine.length(); j++) {
                    JSONObject exObj = routine.getJSONObject(j);
                    int id = exObj.getInt("exerciseId");
                    if (!validIds.contains(id)) continue; // 후보 목록 외 운동 제거

                    Exercise ex = candidates.get(bp).stream()
                            .filter(e -> e.getExerciseId() == id)
                            .findFirst()
                            .orElse(null);
                    if (ex == null) continue;

                    RecommendedExercise re = new RecommendedExercise();
                    re.setExerciseId(id);
                    re.setSets(safeInt(exObj.get("sets")) );
                    re.setReps(safeInt(exObj.get("reps")) );
                    re.setWeight(safeInt(exObj.opt("weight")) );
                    re.setExercise(ex);
                    routineList.add(re);
                }
                result.put(bp, routineList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
