package com.example.glowtales.service;

import com.example.glowtales.domain.Member;
import com.example.glowtales.dto.request.TaleRequest;
import com.example.glowtales.dto.response.tale.TaleDetailResponseDto;
import com.example.glowtales.repository.LanguageRepository;
import com.example.glowtales.repository.LanguageTaleRepository;
import com.example.glowtales.repository.TaleRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class PromptService {

    private final TaleRepository taleRepository;
    private final LanguageTaleRepository languageTaleRepository;
    private final LanguageRepository languageRepository;

    @Value("${chatgpt.api-key}")
    private String apiKey;

    @Value("${chatgpt.api-url}")
    private String apiUrl;

    @Value("${chatgpt.model}")
    private String model;

    public List<TaleDetailResponseDto> createInitialTales(TaleRequest taleRequest, Member member) {
        String prompt =
                "Please generate a fairy tale in JSON format based on the following inputs:\n" +
                        "Atmosphere:" + taleRequest.getMood() + "\n" +
                        "Characters:" + taleRequest.getCharacters() + "(e.g., a brave knight, a curious fox, a kind witch)\n" +
                        "Main Plot/Theme: " + taleRequest.getContents() + "(e.g., a quest for hidden treasure, a journey to discover friendship)\n" +
                        "Reader's Age:" + member.getAge() + " years old\n" +
                        "Preferred Language: English, Korean, Chinese, Japanese\n" +
                        "Keyword: " + taleRequest.getKeywords() + "\n" +
                        """
                                Please generate a fairy tale that adheres to the following requirements:
                                
                                 1. Length: The story should be at least 35 sentences and no more than 50 sentences.
                                 2. Age Appropriateness: Tailor the language, themes, and content to be suitable for the reader's age.
                                 3. Dynamic Content: The story should reflect the atmosphere, characters, main plot/theme, and keyword provided.
                                 4. Title: Generate a suitable title for the fairy tale in the specified languages.
                                 5. JSON Format: The output should be in JSON format as shown below.
                                
                                 Expected JSON Format:
                                
                                 [
                                   {
                                     "language": "English",
                                     "title": "The Lost Kingdom of Glimmervale",
                                     "story": "Once upon a time, in a magical land, there was a brave knight named Sir Cedric..."
                                   },
                                   {
                                     "language": "Korean",
                                     "title": "잃어버린 왕국",
                                     "story": "옛날 옛적에, 마법의 땅에 용감한 기사 세드릭이 살고 있었어요..."
                                   },
                                   {
                                     "language": "Chinese",
                                     "title": "失落的王国",
                                     "story": "很久很久以前，在一个魔法的土地上，有一位勇敢的骑士名叫赛德里克..."
                                   },
                                   {
                                     "language": "Japanese",
                                     "title": "失われた王国",
                                     "story": "昔々、魔法の国にセドリックという勇敢な騎士がいました..."
                                   }
                                 ]
                                """;


        return getStories(getChatResponse(prompt));
    }

    public List<TaleDetailResponseDto> test() {
        String prompt = "Please generate a fairy tale based on the following inputs:\n" +
                "\n" +
                "Atmosphere: Magical\n" +
                "Characters: A brave knight, a wise owl, an enchanted tree\n" +
                "Main Plot/Theme: A quest to find a lost kingdom\n" +
                "Reader's Age: 10 years old\n" +
                "Preferred Language: English, Korean\n" +
                "Keyword: A glowing crystal\n" +
                """
                        Please generate a fairy tale that adheres to the following requirements:
                        
                         1. Length: The story should be at least 35 sentences and no more than 50 sentences.
                         2. Age Appropriateness: Tailor the language, themes, and content to be suitable for the reader's age.
                         3. Dynamic Content: The story should reflect the atmosphere, characters, main plot/theme, and keyword provided.
                         4. Title: Generate a suitable title for the fairy tale in the specified languages.
                         5. Formatting: Separate each sentence with a newline character "\n".
                         6. JSON Format: The output should be in JSON format as shown below.
                        
                         Expected JSON Format:
                        
                         [
                           {
                             "language": "English",
                             "title": "The Lost Kingdom of Glimmervale",
                             "story": "Once upon a time, in a magical land, there was a brave knight named Sir Cedric..."
                           },
                           {
                             "language": "Korean",
                             "title": "잃어버린 왕국",
                             "story": "옛날 옛적에, 마법의 땅에 용감한 기사 세드릭이 살고 있었어요..."
                           },
                           {
                             "language": "Chinese",
                             "title": "失落的王国",
                             "story": "很久很久以前，在一个魔法的土地上，有一位勇敢的骑士名叫赛德里克..."
                           },
                           {
                             "language": "Japanese",
                             "title": "失われた王国",
                             "story": "昔々、魔法の国にセドリックという勇敢な騎士がいました..."
                           }
                         ]
                        """;
        return getStories(getChatResponse(prompt));
    }

    private String getChatResponse(String prompt) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", "application/json");

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(Map.of("role", "user", "content", prompt))
//                "max_tokens", 1500
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);
        System.out.println("response: " + response.getBody());
        return response.getBody();
    }

    public List<TaleDetailResponseDto> getStories(String responseBody) {
        try {
            // JSON 응답에서 'content' 값을 추출
            String content = extractContent(responseBody);

            // JSON 문자열을 JSONArray로 변환하고 각 항목을 출력
            org.json.JSONArray jsonArray = new JSONArray(content);

            List<TaleDetailResponseDto> tales = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                System.out.println("title: " + jsonObject.optString("title", "Untitled"));
                System.out.println("language: " + jsonObject.optString("language", "Unknown"));
                System.out.println("story: " + jsonObject.optString("story", "No story available"));
                tales.add(
                        new TaleDetailResponseDto(
                                jsonObject.optString("title", "Untitled"),
                                jsonObject.optString("language", "Unknown"),
                                jsonObject.optString("story", "No story available")
                        ));
            }
            return tales;
        } catch (Exception e) {
            e.printStackTrace();  // 로깅 또는 적절한 예외 처리 필요
        }
        return new ArrayList<>();
    }

    // 'content' 값을 추출하는 메서드
    private String extractContent(String responseBody) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(responseBody);
        JsonNode contentNode = rootNode.path("choices").get(0).path("message").path("content");
        System.out.println("content: " + contentNode);
        return contentNode.asText().replace("```json", "").replace("```", "").trim();
    }


}
