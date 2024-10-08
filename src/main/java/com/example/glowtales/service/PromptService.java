package com.example.glowtales.service;

import com.example.glowtales.domain.Member;
import com.example.glowtales.dto.response.tale.TaleDetailResponseDto;
import com.example.glowtales.dto.response.tale.TaleForm;
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

    @Value("${chatgpt.api-key}")
    private String apiKey;

    @Value("${chatgpt.api-url}")
    private String apiUrl;

    @Value("${chatgpt.model}")
    private String model;

    public List<TaleDetailResponseDto> createInitialTales(TaleForm taleForm, Member member) {
        StringBuilder characters = new StringBuilder();
        for (String character : taleForm.getCharacters()) {
            characters.append(character).append(", ");
        }

        if (!characters.isEmpty()) {
            characters.setLength(characters.length() - 2);
        }

        StringBuilder keywords = new StringBuilder();
        for (String keyword : taleForm.getKeywords()) {
            characters.append(keyword).append(", ");
        }

        if (!keywords.isEmpty()) {
            keywords.setLength(keywords.length() - 2);
        }

        String prompt =
                "\"Please generate a fairy tale in JSON format based on the following inputs:\n" +
                        "\n" +
                        "Atmosphere: \"" + taleForm.getMood() + "\n" +
                        "Characters: \"" + characters + "\"(e.g., a brave knight, a curious fox, a kind witch)\n" +
                        "Main Plot/Theme: \"" + taleForm.getContents() + "\"(e.g., a quest for hidden treasure, a journey to discover friendship)\n" +
                        "Reader's Age: \"" + member.getAge() + "\" years old\n" +
                        "Preferred Language: English, Korean, Chinese, Japanese\n" +
                        "Keyword: \"" + keywords + "\n" +
                        "Please generate a fairy tale that adheres to the following requirements:\n" +
                        "\n" +
                        "Length: The story should be at least 35 sentences and no more than 50 sentences.\n" +
                        "Age Appropriateness: Tailor the language, themes, and content to be suitable for the reader's age.\n" +
                        "Character Names: Ensure that the character names are friendly and appealing to children.\n" +
                        "Dynamic Content: The story should reflect the atmosphere, characters, main plot/theme, and keyword provided.\n" +
                        "Keywords Appearance: The keywords should appear as objects in the fairy tale but should not have dialogue or be personified.\n" +
                        "Sentence Separation: Each sentence should be separated by a newline character (\\n) for clarity.\n" +
                        "Title: Generate a suitable title for the fairy tale in the specified languages.\n" +
                        "JSON Format: The output should be in JSON format as shown below.\n" +
                        "Expected JSON Format:\n" +
                        "\n" +
                        "json\n" +
                        "[\n" +
                        "  {\n" +
                        "    \"language\": \"English\",\n" +
                        "    \"title\": \"The Lost Kingdom of Glimmervale\",\n" +
                        "    \"story\": \"Once upon a time, in a magical land, there was a brave knight named Sir Cedric...\\nSir Cedric ventured into the forest...\\nHe discovered a hidden treasure...\\n\"\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"language\": \"Korean\",\n" +
                        "    \"title\": \"잃어버린 왕국\",\n" +
                        "    \"story\": \"옛날 옛적에, 마법의 땅에 용감한 기사 세드릭이 살고 있었어요...\\n세드릭은 숲으로 모험을 떠났어요...\\n그는 숨겨진 보물을 발견했어요...\\n\"\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"language\": \"Chinese\",\n" +
                        "    \"title\": \"失落的王国\",\n" +
                        "    \"story\": \"很久很久以前，在一个魔法的土地上，有一位勇敢的骑士名叫赛德里克...\\n赛德里克走进了森林...\\n他发现了隐藏的宝藏...\\n\"\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"language\": \"Japanese\",\n" +
                        "    \"title\": \"失われた王国\",\n" +
                        "    \"story\": \"昔々、魔法の国にセドリックという勇敢な騎士がいました...\\nセドリックは森に冒険に出かけました...\\n彼は隠された宝物を見つけました...\\n\"\n" +
                        "  }\n" +
                        "]";


        return getStories(getChatResponse(prompt));
    }

    public JSONObject createQuiz(String tale, String learningLevel) throws Exception {
        String prompt = "\"You are an AI assistant that helps create quizzes based on fairy tales. I will provide you with a fairy tale text and specify the learning level of the students. Your task is to extract keywords and key sentences from the story, and then generate a quiz based on the provided learning level. Please follow the structure below for your response:\n" +
                "\n" +
                "### Parameters:\n" +
                "- **Fairy Tale Text**: " + tale + "\n" +
                "- **Learning Level**: " + getLearningLevel(learningLevel) + "\n" +
                "\n" +
                "### Instructions:\n" +
                "1. **Extract Keywords:**\n" +
                "\"- Identify 5 important words from the provided fairy tale text.\",\n" +
                "\"- Make sure the words are in the original language of the fairy tale.\",\n" +
                "\"- Adjust the complexity of the selected words according to the provided learning level:\",\n" +
                "\"  - For beginners, choose simpler and more frequently used words.\",\n" +
                "\"  - For intermediate learners, choose moderately complex words that are still commonly used.\",\n" +
                "\"  - For advanced learners, choose more complex or less commonly used words.\",\n" +
                "\"- For each word, provide the word in the original language and its meaning in Korean.\""+
        "\n" +
                "2. **Extract Key Sentences:**\n" +
                "   - Identify 5 important sentences from the provided fairy tale text.\n" +
                "   - Adjust the complexity of the selected sentences according to the provided learning level:\n" +
                "     - For beginners, choose shorter and simpler sentences.\n" +
                "     - For intermediate learners, choose moderately complex sentences with some challenging vocabulary or structures.\n" +
                "     - For advanced learners, choose longer and more complex sentences.\n" +
                "   - Provide each sentence in the original language along with its meaning in Korean.\n" +
                "\n" +
                "3. **Generate Quizzes** (consider the provided learning level):\n" +
                " \"- Create 3 multiple-choice questions using the extracted keywords. For each question, provide 4 incorrect choices (in Korean) and 1 correct choice (in Korean). If the original language is in Chinese, there are cases where the choice translation is not good in Korean, but it must be given in Korean. \",\n" +
                " \"- Create 2 short-answer questions using the extracted keywords. Provide only the keyword (in original language) and answer in korean for each question.\",\n" +
                " \"- Create 5 sentence ordering questions using the extracted key sentences.\",\n" +
                " \"- For each sentence ordering question, provide question(the sentense meaning) in Korean and the segments in the original language of the fairy tale.\""+
                "### Example Structure:\n" +
                "```json\n" +
                "{\n" +
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
                "        }, \n" +
                "        {\n" +
                "            \"order\": {\n" +
                "                \"quiz\": \"빛나는 크리스탈을 찾는 걸 도와줄 수 있어?\",\n" +
                "                \"sequences\": [\"Can you help me\", \"find the glowing crystal?\"]\n" +
                "            }\n" +
                "        }, \n" +
                "        {\n" +
                "            \"order\": {\n" +
                "                \"quiz\": \"나무는 숲의 비밀을 속삭였다.\",\n" +
                "                \"sequences\": [\"The tree whispered\", \"secrets of the forest.\"]\n" +
                "            }\n" +
                "        }, \n" +
                "        {\n" +
                "            \"order\": {\n" +
                "                \"quiz\": \"왕자와 공주의 사랑은 마법 같았다.\",\n" +
                "                \"sequences\": [\"The love between\", \"the prince and the princess\", \"was magical.\"]\n" +
                "            }\n" +
                "        }, \n" +
                "        {\n" +
                "            \"order\": {\n" +
                "                \"quiz\": \"그들은 고대 나무 아래 숨겨진 크리스탈을 발견했다.\",\n" +
                "                \"sequences\": [\"They found the crystal\", \"hidden under\", \"the ancient tree.\"]\n" +
                "            }\n" +
                "        }\n" +
                "    ]\n" +
                "}\n";

        return new JSONObject(extractContent(getChatResponse(prompt)));
    }

    private String getLearningLevel(String learningLevel) {
        if (learningLevel.equals("1000")) {
            return "I am just starting to learn.";
        } else if (learningLevel.equals("2000")) {
            return "I know a few commonly used words.";
        } else if (learningLevel.equals("3000")) {
            return "I can have basic conversations.";
        } else if (learningLevel.equals("4000")) {
            return "I can discuss a variety of topics.";
        } else if (learningLevel.equals("5000")) {
            return "I can discuss most topics in detail.";
        }
        return "";
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
                   - For each word, provide the word in the original language  and its meaning in Korean.
                
                2. **Extract Key Sentences:**
                   - Identify 5 important sentences from the provided fairy tale text.
                   - Provide each sentence in the original language along with its meaning in Korean.
                
                3. **Generate Quizzes** (consider the provided learning level):
                   - Create 3 multiple-choice questions using the extracted keywords. For each question, provide 4 incorrect choices (in Korean) and 1 correct choice (in Korean). Please make sure there are no overlapping incorrect choices.
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
