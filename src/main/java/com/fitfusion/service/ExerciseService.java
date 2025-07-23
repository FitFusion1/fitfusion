package com.fitfusion.service;

import com.fitfusion.vo.Exercise;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitfusion.mapper.ExerciseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class ExerciseService {

    @Autowired
    private ExerciseMapper exerciseMapper;
    private final RestTemplate restTemplate;
    @Autowired
    private final ObjectMapper objectMapper;

    @Value("${openai.api.key}")
    private String openaiApiKey;

    public ExerciseService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public List<Exercise> getAllExercises() {
        return exerciseMapper.findAllExercises();
    }

    public Exercise getExerciseById(int exerciseId) {
        return exerciseMapper.findExerciseById(exerciseId);
    }

    public List<Map<String, String>> fetchExerciseInfoWithFallback() {
        List<Map<String, String>> result = new ArrayList<>();
        String url = "https://wger.de/api/v2/exerciseinfo/?limit=100&offset=0";

        JsonNode root = restTemplate.getForObject(url, JsonNode.class);
        JsonNode exercises = root.get("results");

        if (exercises != null && exercises.isArray()) {
            for (JsonNode node : exercises) {
                String name = extractBestTranslation(node.get("translations"), "name");
                String description = extractBestTranslation(node.get("translations"), "description");
                String rawCategory = node.has("category") ? node.get("category").get("name").asText("") : "";
                String category = mapCategory(rawCategory);
                category = detectCardioOrStretchCategory(name, description, category);

                String equipment = extractNames(node.get("equipment"));
                String mainParts = extractMuscleNames(node.get("muscles"));

                String prompt = String.format("""
                    다음 운동 정보를 한국어로 자연스럽게 번역하고, 부족한 내용은 일반적인 지식을 바탕으로 보완해 주세요.

                    [운동 이름]: %s
                    [운동 설명]: %s
                    [운동 부위]: %s
                    [사용 장비]: %s
                    [카테고리]: %s

                    요청 조건:

                    - 운동 이름은 간결한 명사형 표현으로 작성하며, 실제 검색 시 자주 쓰이는 자연스러운 이름 사용 (예: 벤치 프레스, 덤벨 숄더 프레스)
                    - 이름에 불필요한 단어나 인사말(- 헬로 등)은 포함하지 말 것

                    - 운동 설명은 반드시 '1. ~', '2. ~' 형식의 번호 단계로만 구성
                    - 각 단계는 줄바꿈 없이 한 줄로 이어서 작성 (예: 1. ~ 2. ~ 3. ~ 형태), 줄바꿈 문자(\\n) 포함 금지
                    - **운동을 실제 수행하는 단계별 동작**만 포함할 것
                    - 운동 효과, 장점, 배경 설명은 절대 작성하지 말 것
                    - 예: "이 운동은 초보자에게 좋습니다", "삼두근을 강화합니다" → 작성 금지
                    - 설명이 부족하거나 비어 있을 경우 일반적인 상식에 기반하여 보완
                    - HTML 태그 없이 **텍스트만 출력**

                    - 운동 부위는 반드시 **해부학적으로 지정된 정확한 한국어 근육명**으로 작성 (예: 삼두근, 광배근, 대퇴사두근 등)
                    - 모든 근육 명칭은 **영문이 아닌 한국어로 정확하게 표기** (예: trapezius → 승모근, lats → 광배근)
                    - '팔근육', '허벅지' '어깨근육' '팔뚝근육' '장딴지근육' 등 **모호한 표현은 금지**
                    - 모든 근육 명칭은 반드시 해부학적으로 정확한 **한국어 표현으로**, **띄어쓰기 없이 붙여 씀** (예: 전면삼각근, 상완이두근, 대퇴사두근, 승모근)
                    - 아래와 같은 **잘못된 띄어쓰기 예시**를 절대 포함하지 말 것: (전면 삼각근, 상완 이두근, 대퇴 사두근, 승모 근 등)
                    - 예외 없이 근육 이름은 **모두 붙여서 작성** (예: '광배근' O, '광 배 근' X)
                    - 근육명은 모두 **전문 용어 표기 규칙**을 따라야 하며, **절대 띄어쓰지 말 것** (예: 전면삼각근 O, 전면 삼각근 X)

                    - 카테고리는 다음 중 하나로 지정: **등, 가슴, 어깨, 하체, 복근, 팔, 유산소, 스트레칭**
                      - 아래 기준에 해당하면 '유산소'로 분류:
                       · 비교적 낮은 강도로 반복적으로 수행하는 장시간 운동
                       · 심박수와 호흡이 증가하는 전신 또는 하체 중심 운동
                       · 예: 달리기, 걷기, 자전거 타기, 줄넘기, 실내 사이클, 계단 오르기 등
                     - 아래 기준에 해당하면 '스트레칭'으로 분류:
                       · 유연성 향상, 관절 가동성 개선, 부상 방지 목적
                       · 정적 또는 동적 스트레칭, 쿨다운/워밍업 포함
                     - 유산소나 스트레칭이 아닌 경우, 주로 사용하는 부위에 따라 적절한 카테고리로 지정

                    - 카테고리는 넓은 신체 부위, 운동 부위는 구체적인 근육 명칭으로 구분
                    - 장비, 부위, 카테고리는 일반인이 쉽게 이해할 수 있도록 용어를 변환해 작성 (예: 전면 델토이드 → 전면 삼각근, 케이블 기계 → 케이블 기구)
                    - 장비 명칭에 띄어쓰기 금지

                    - 마지막으로, 이 운동의 피로도 수준을 1~5 중 하나의 숫자로 fatique_level 항목으로 작성:
                          · 1 = 매우 낮음: 스트레칭, 요가, 걷기 등 매우 저강도
                          · 2 = 낮음: 맨몸 스쿼트, 플랭크, 쉬운 코어 운동 등 저강도
                          · 3 = 중간: 덤벨, 기구 등을 사용하는 중간 강도 운동
                          · 4 = 높음: 중량 운동, 복합 관절 웨이트 등 강한 피로감
                          · 5 = 매우 높음: 크로스핏, 데드리프트, 고강도 인터벌 등 매우 강도 높은 운동
                    """, name, description, mainParts, equipment, category);

                Map<String, String> translated = callOpenAi(prompt);
                result.add(translated);
            }
        }

        return result;
    }

    @Transactional
    public void saveExercisesToDb() {
        List<Map<String, String>> list = fetchExerciseInfoWithFallback();

        for (Map<String, String> data : list) {
            Exercise exercise = new Exercise();
            exercise.setExerciseName(data.get("name"));
            exercise.setDescription(data.get("description"));
            exercise.setEquipment(data.get("equipment"));
            exercise.setMainParts(data.get("main_parts"));
            exercise.setCategory(data.get("category"));

            // name 값 비어 있는 경우 저장 스킵
            if (data.get("name") == null || data.get("name").isBlank()) {
                System.out.println("[⚠ 저장 생략 - name 없음]: " + data);
                continue; // 이 항목은 건너뜀
            }
            if ("스트레칭".equals(data.get("category"))) {
                exercise.setFatigueLevel(1);
            } else {
                // ⚠ fatique_level이 null이거나 비어 있으면 오류 발생함
                try {
                    exercise.setFatigueLevel(Integer.parseInt(data.get("fatique_level")));
                } catch (Exception e) {
                    System.out.println("[⚠ fatique_level 파싱 오류]: " + data.get("fatique_level"));
                    exercise.setFatigueLevel(0); // 기본값 넣어도 됨
                }
            }
            // 저장 시도 로그
            System.out.println("[>> DB 저장 시도]: " + exercise.getExerciseName());

            try {
                exerciseMapper.insertExercise(exercise);
                System.out.println("[✔ 저장 완료]");
            } catch (Exception e) {
                System.out.println("[❌ 저장 실패]: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }



    private String extractBestTranslation(JsonNode translations, String field) {
        List<Integer> langPriority = Arrays.asList(2, 1, 3, 4, 5, 6, 7, 8);
        for (Integer lang : langPriority) {
            for (JsonNode t : translations) {
                if (t.get("language").asInt() == lang && t.hasNonNull(field)) {
                    return t.get(field).asText("");
                }
            }
        }
        return "";
    }

    private String extractNames(JsonNode array) {
        if (array == null || !array.isArray()) return "";
        List<String> names = new ArrayList<>();
        for (JsonNode item : array) {
            if (item.has("name")) {
                names.add(item.get("name").asText(""));
            }
        }
        return String.join(", ", names);
    }

    private String extractMuscleNames(JsonNode array) {
        if (array == null || !array.isArray()) return "";
        List<String> names = new ArrayList<>();
        for (JsonNode muscle : array) {
            if (muscle.has("name")) {
                names.add(muscle.get("name").asText(""));
            }
        }
        return String.join(", ", names);
    }

    private Map<String, String> callOpenAi(String prompt) {
        String url = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openaiApiKey);

        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-4");
        body.put("messages", Collections.singletonList(message));
        body.put("temperature", 0.5);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.POST, entity, JsonNode.class);
            String content = response.getBody()
                    .get("choices").get(0)
                    .get("message")
                    .get("content")
                    .asText();

            System.out.println("[OpenAI 응답]\n" + content);
            return parseTranslation(content);
        } catch (Exception e) {
            Map<String, String> fallback = new HashMap<>();
            fallback.put("name", "");
            fallback.put("description", "");
            fallback.put("equipment", "");
            fallback.put("main_parts", "");
            fallback.put("category", "");
            return fallback;
        }
    }

    private Map<String, String> parseTranslation(String content) {
        Map<String, String> result = new HashMap<>();
        String[] lines = content.split("\\n");

        String name = "", description = "", equipment = "", mainParts = "", category = "", fatiqueLevel = "";
        StringBuilder descBuilder = new StringBuilder();

        for (String line : lines) {
            String lower = line.toLowerCase();

            if (lower.contains("운동 이름")) {
                name = extractField(line);
            } else if (lower.contains("운동 설명")) {
                description = extractField(line);
            } else if (lower.contains("사용 장비")) {
                equipment = extractField(line);
            } else if (lower.contains("운동 부위")) {
                mainParts = extractField(line);
            } else if (lower.contains("카테고리")) {
                category = extractField(line);
            } else if (lower.contains("fatique_level") || lower.contains("피로도")) {
                fatiqueLevel = extractField(line).replaceAll("[^0-5]", ""); // 숫자만 추출
            } else if (!line.trim().isEmpty()) {
                descBuilder.append(" ").append(line.trim());
            }

        }

        if (!descBuilder.toString().isBlank()) description += " " + descBuilder;

        result.put("name", name.trim());
        result.put("description", description.trim());
        result.put("equipment", equipment.trim());
        result.put("main_parts", mainParts.trim());
        result.put("category", category.trim());
        result.put("fatique_level", fatiqueLevel.trim());
        return result;
    }

    private String extractField(String line) {
        String[] parts = line.split(":", 2);
        return (parts.length > 1) ? parts[1].trim() : "";
    }

    private String mapCategory(String raw) {
        String lower = raw.toLowerCase();
        if (lower.contains("back") || lower.contains("lats") || lower.contains("rear")) return "등";
        if (lower.contains("chest") || lower.contains("pectoral")) return "가슴";
        if (lower.contains("shoulder") || lower.contains("deltoid")) return "어깨";
        if (lower.contains("leg") || lower.contains("thigh") || lower.contains("glute")) return "하체";
        if (lower.contains("ab") || lower.contains("core") || lower.contains("stomach")) return "복근";
        if (lower.contains("arm") || lower.contains("biceps") || lower.contains("triceps")) return "팔";
        if (lower.contains("cardio") || lower.contains("aerobic")) return "유산소";
        return "기타";
    }

    private String detectCardioOrStretchCategory(String name, String description, String fallbackCategory) {
        String text = (name + " " + description).toLowerCase();

        Set<String> stretchingKeywords = Set.of(
                "stretch", "stretching", "mobility", "flexibility", "hip opener", "cool down", "warm up"
        );

        Set<String> cardioKeywords = Set.of(
                "jump", "run", "jog", "step", "burpee", "cycling",
                "mountain climber", "high knees", "jumping jack", "aerobic", "cardio",
                "skipping", "stair", "rower", "rowing", "box jump"
        );

        for (String keyword : stretchingKeywords) {
            if (text.contains(keyword)) {
                return "스트레칭";
            }
        }

        for (String keyword : cardioKeywords) {
            if (text.contains(keyword)) {
                return "유산소";
            }
        }

        return fallbackCategory;
    }
}
