package com.example.glowtales.service;

import com.example.glowtales.domain.Member;
import com.example.glowtales.dto.request.TaleForm;
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

    public List<TaleDetailResponseDto> createInitialTales(TaleForm taleForm, Member member) {

        String characters = "";
        for (String character : taleForm.getCharacters()) {
            characters += character;
        }

        String keywords = "";
        for (String keyword : taleForm.getKeywords()) {
            keywords += keyword;
        }

        String prompt =
                "Please generate a fairy tale in JSON format based on the following inputs:\n" +
                        "Atmosphere:" + taleForm.getMood() + "\n" +
                        "Characters:" + characters + "(e.g., a brave knight, a curious fox, a kind witch)\n" +
                        "Main Plot/Theme: " + taleForm.getContents() + "(e.g., a quest for hidden treasure, a journey to discover friendship)\n" +
                        "Reader's Age:" + member.getAge() + " years old\n" +
                        "Preferred Language: English, Korean, Chinese, Japanese\n" +
                        "Keyword: " + keywords + "\n" +
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
                "Preferred Language: English, Korean, Chinese, Japanese\n" +
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

    public void createQuiz(String tale, String learningLevel) {
        String prompt = "You are an AI assistant that helps create quizzes based on fairy tales. I will provide you with a fairy tale text and specify the learning level of the students. Your task is to extract keywords and key sentences from the story, and then generate a quiz based on the provided learning level. Please follow the structure below for your response:\n" +
                "\n" +
                "### Parameters:\n" +
                "- **Fairy Tale Text**: " + tale + "\n" +
                "- **Learning Level**: " + learningLevel + "\n" +
                "\n" +
                "### Instructions:\n" +
                "1. **Extract Keywords:**\n" +
                "   - Identify 5 important words from the provided fairy tale text.\n" +
                "   - For each word, provide the word in the original language and its meaning in Korean.\n" +
                "\n" +
                "2. **Extract Key Sentences:**\n" +
                "   - Identify 5 important sentences from the provided fairy tale text.\n" +
                "   - Provide each sentence in the original language along with its meaning in Korean.\n" +
                "\n" +
                "3. **Generate Quizzes** (consider the provided learning level):\n" +
                "   - Create 3 multiple-choice questions using the extracted keywords. For each question, provide 4 incorrect choices (in Korean) and 1 correct choice (in Korean).\n" +
                "   - Create 2 short-answer questions using the extracted keywords. Provide only the keyword (in the original language) for each question.\n" +
                "   - Create 1 sentence ordering question using the extracted key sentences. Break the sentence into 3 to 7 parts and present them out of order. The student will need to rearrange them into the correct order.\n" +
                "\n" +
                "### Example Structure:\n" +
                "```json\n" +
                "{\n" +
                "    \"fairyTale\": \"[Insert the provided fairy tale text here]\",\n" +
                "    \"learningLevel\": \"[Insert the provided learning level here]\",\n" +
                "    \"keywords\": [\n" +
                "        [\"tree\", \"나무\"],\n" +
                "        [\"love\", \"사랑\"],\n" +
                "        [\"magic\", \"마법\"],\n" +
                "        [\"crystal\", \"크리스탈\"],\n" +
                "        [\"river\", \"강\"]\n" +
                "    ],\n" +
                "    \"keySentences\": [\n" +
                "        [\"Can you help me find the glowing crystal?\", \"빛나는 크리스탈을 찾는 걸 도와줄 수 있어?\"],\n" +
                "        [\"The tree whispered secrets of the forest.\", \"나무는 숲의 비밀을 속삭였다.\"],\n" +
                "        [\"She followed the river until she reached the enchanted forest.\", \"그녀는 마법의 숲에 도착할 때까지 강을 따라갔다.\"],\n" +
                "        [\"The love between the prince and the princess was magical.\", \"왕자와 공주의 사랑은 마법 같았다.\"],\n" +
                "        [\"They found the crystal hidden under the ancient tree.\", \"그들은 고대 나무 아래 숨겨진 크리스탈을 발견했다.\"]\n" +
                "    ],\n" +
                "    \"quizzes\": [\n" +
                "        {\n" +
                "            \"multiple\": {\n" +
                "                \"word\": \"tree\",\n" +
                "                \"choices\": [\"나무\", \"꽃\", \"채소\", \"식물\", \"숲\"],\n" +
                "                \"answer\": \"나무\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"multiple\": {\n" +
                "                \"word\": \"love\",\n" +
                "                \"choices\": [\"사랑\", \"미움\", \"우정\", \"동정\", \"열정\"],\n" +
                "                \"answer\": \"사랑\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"multiple\": {\n" +
                "                \"word\": \"crystal\",\n" +
                "                \"choices\": [\"크리스탈\", \"다이아몬드\", \"유리\", \"보석\", \"물\"],\n" +
                "                \"answer\": \"크리스탈\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"short\": {\n" +
                "                \"word\": \"magic\",\n" +
                "                \"answer\": \"마법\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"short\": {\n" +
                "                \"word\": \"river\",\n" +
                "                \"answer\": \"강\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"order\": {\n" +
                "                \"quiz\": \"마법의 숲에 도착할 때까지 강을 따라가\",\n" +
                "                \"sequences\": [\"Follow the river\", \"until\", \"you reach\", \"the Enchanted Forest\"]\n" +
                "            }\n" +
                "        }\n" +
                "    ]\n" +
                "}\n";
    }

    public JSONObject testQuiz() throws Exception {
        String prompt = """
                You are an AI assistant that helps create quizzes based on fairy tales. I will provide you with a fairy tale text and specify the learning level of the students. Your task is to extract keywords and key sentences from the story, and then generate a quiz based on the provided learning level. Please follow the structure below for your response:
                
                ### Parameters:
                - **Fairy Tale Text**: Once upon a time, in a magical land, there was a brave knight named Sir Cedric. He wore shining armor and had a heart as pure as gold. One day, he heard a tale from a wise old owl perched in a tall tree. "There is a lost kingdom called Glimmervale," the owl hooted. "It vanished many years ago, hidden by a powerful enchantment. To find it, you must seek a glowing crystal that holds its secret."  Sir Cedric's eyes sparkled with determination. "I will find this crystal and restore the kingdom!" he declared. The wise owl flapped its wings and said, "Follow the river until you reach the Enchanted Forest." With a grateful nod, Sir Cedric set off on his quest. As he walked, the sun shined brightly, lighting up his path. He soon reached the Enchanted Forest where ancient trees whispered secrets. In the heart of the forest, he spotted a great enchanted tree. It was covered in twinkling lights and seemed to breathe magic. He approached the tree, feeling its warm energy surrounding him. "Oh, noble tree!" Sir Cedric called. "Can you help me find the glowing crystal?"  The enchanted tree responded in a deep, calming voice, "To find the crystal, you must prove your bravery." "What must I do?" he asked eagerly. "Defeat the dark shadow that guards the crystal," the tree replied.  Without hesitation, Sir Cedric nodded, ready to face the challenge. He ventured deeper into the forest, where shadows danced around him. Finally, he confronted the dark shadow, a creature made of swirling mist. "You cannot take the crystal!" it hissed, baring sharp teeth. With his sword raised high, Cedric fought fiercely, remembering the tree's strength. After a fierce battle, he struck a brave blow, and the shadow vanished into thin air. Sir Cedric hurried to a nearby cave where the glowing crystal rested. It pulsed with beautiful colors, illuminating the whole cave. He carefully picked it up, feeling warmth spread through his hands. As he held it, the crystal whispered secrets of the lost kingdom. With newfound courage, he returned to the enchanted tree. "I found the crystal!" he exclaimed, excitement in his voice. The tree glowed brighter, and suddenly, the air shimmered. Before him, a portal opened, revealing the beautiful kingdom of Glimmervale. Sir Cedric stepped forward, and he was welcomed by joyful villagers. Together, they celebrated the return of their kingdom, thanks to a brave knight and a glowing crystal.
                - **Learning Level**: 기본적인 대화를 할 수 있어요
                
                ### Instructions:
                1. **Extract Keywords:**
                   - Identify 5 important words from the provided fairy tale text.
                   - For each word, provide the word in the original language and its meaning in Korean.
                
                2. **Extract Key Sentences:**
                   - Identify 5 important sentences from the provided fairy tale text.
                   - Provide each sentence in the original language along with its meaning in Korean.
                
                3. **Generate Quizzes** (consider the provided learning level):
                   - Create 3 multiple-choice questions using the extracted keywords. For each question, provide 4 incorrect choices (in Korean) and 1 correct choice (in Korean).
                   - Create 2 short-answer questions using the extracted keywords. Provide only the keyword (in the original language) for each question.
                   - Create 1 sentence ordering question using the extracted key sentences. Break the sentence into 3 to 7 parts and present them out of order. The student will need to rearrange them into the correct order.
                
                ### Example Structure:
                ```json
                [
                    "keywords": [
                        ["tree", "나무"],
                        ["love", "사랑"],
                        ["magic", "마법"],
                        ["crystal", "크리스탈"],
                        ["river", "강"]
                    ],
                    "keySentences": [
                        ["Can you help me find the glowing crystal?", "빛나는 크리스탈을 찾는 걸 도와줄 수 있어?"],
                        ["The tree whispered secrets of the forest.", "나무는 숲의 비밀을 속삭였다."],
                        ["She followed the river until she reached the enchanted forest.", "그녀는 마법의 숲에 도착할 때까지 강을 따라갔다."],
                        ["The love between the prince and the princess was magical.", "왕자와 공주의 사랑은 마법 같았다."],
                        ["They found the crystal hidden under the ancient tree.", "그들은 고대 나무 아래 숨겨진 크리스탈을 발견했다."]
                    ],
                    "quizzes": [
                        {
                            "multiple": {
                                "word": "tree",
                                "choices": ["나무", "꽃", "채소", "식물", "숲"],
                                "answer": "나무"
                            }
                        },
                        {
                            "multiple": {
                                "word": "love",
                                "choices": ["사랑", "미움", "우정", "동정", "열정"],
                                "answer": "사랑"
                            }
                        },
                        {
                            "multiple": {
                                "word": "crystal",
                                "choices": ["크리스탈", "다이아몬드", "유리", "보석", "물"],
                                "answer": "크리스탈"
                            }
                        },
                        {
                            "short": {
                                "word": "magic",
                                "answer": "마법"
                            }
                        },
                        {
                            "short": {
                                "word": "river",
                                "answer": "강"
                            }
                        },
                        {
                            "order": {
                                "quiz": "마법의 숲에 도착할 때까지 강을 따라가",
                                "sequences": ["Follow the river", "until", "you reach", "the Enchanted Forest"]
                            }
                        }
                    ]
                ]
                """;
        return new JSONObject(extractContent(getChatResponse(prompt)));
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
