package com.example.glowtales.service;

import com.example.glowtales.domain.Member;
import com.example.glowtales.domain.Tale;
import com.example.glowtales.dto.request.TranslationRequest;
import com.example.glowtales.dto.response.HomeInfoResponseDto;
import com.example.glowtales.dto.response.TaleResponseDto;
import com.example.glowtales.dto.response.TranslationResponse;
import com.example.glowtales.dto.response.WordResponseDto;
import com.example.glowtales.repository.MemberRepository;
import com.example.glowtales.repository.TaleRepository;
import com.example.glowtales.repository.WordRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TaleService {

    private final TaleRepository taleRepository;
    private final MemberRepository memberRepository;

    private final WordRepository wordRepository;

    private static final Logger logger = LoggerFactory.getLogger(TaleService.class);

//    @Autowired
//    public TaleService(TaleRepository taleRepository, MemberRepository memberRepository,WordRepository wordRepository) {
//        this.taleRepository = taleRepository;
//        this.memberRepository=memberRepository;
//        this.wordRepository=wordRepository;
//    }

    //#001 전체 동화 상태창 불러오기
    public HomeInfoResponseDto getHomeInfoByMemberId(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));
        return new HomeInfoResponseDto(member);
    }


    //#005 완료하지 않은 동화 모두 불러오기
    public List<TaleResponseDto> getUnlearnedTaleByMemberId(Long memberId,int count) {
        List<Tale> tales = taleRepository.findByMemberId(memberId);

        Stream<Tale> taleStream = tales.stream()
                .filter(tale -> tale.getLanguageTaleList().stream()
                        .anyMatch(languageTale -> languageTale.getLanguage().getId() == 1 && languageTale.getIsLearned().getValue() == 2))
                .sorted(Comparator.comparing(Tale::getStudiedAt).reversed());

        if (count > 0) {
            taleStream = taleStream.limit(count);
        }

        return taleStream
                .map(TaleResponseDto::new)
                .collect(Collectors.toList());
    }


    //#007 최근 학습한 동화 모두 불러오기
    public List<TaleResponseDto> getStudiedTaleByMemberId(Long memberId, int count) {
        List<Tale> tales = taleRepository.findByMemberId(memberId);

        Stream<Tale> taleStream = tales.stream()
                .filter(tale -> tale.getLanguageTaleList().stream()
                        .anyMatch(languageTale -> languageTale.getLanguage().getId() == 1 && languageTale.getIsLearned().getValue() == 1))
                .sorted(Comparator.comparing(Tale::getStudiedAt).reversed());

        if (count > 0) {
            taleStream = taleStream.limit(count);
        }

        return taleStream
                .map(TaleResponseDto::new)
                .collect(Collectors.toList());
    }

    //#003 단어장 조회
    public List<WordResponseDto> getWordByMemberId(Long memberId, int count) {
        List<Tale> tales = taleRepository.findByMemberId(memberId);
        Stream<WordResponseDto> wordStream = tales.stream()
                .flatMap(tale ->
                        tale.getTaleWordList().stream()
                                .map(tw -> tw.getWord()) // Word 객체를 스트림으로 변환
                                .filter(word -> word != null && word.getOriginWord() != null)
                                .map(WordResponseDto::new)
                );

        if (count > 0) {
            wordStream = wordStream.limit(count); // count가 0보다 큰 경우에만 limit 적용
        }

        return wordStream.collect(Collectors.toList()); // 리스트로 수집
    }

    //#010 사진에서 키워드 추출하기
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${api.etri.open-api-url}")
    private String openApiUrl;

    @Value("${api.etri.access-key}")
    private String accessKey;

    @Value("${api.deepL.open-api-url}")
    private  String deeplApiUrl;

    @Value("${api.deepL.access-key}")
    private  String authKey;

    public String getKeyword(MultipartFile file) throws IOException {
        byte[] fileBytes = file.getBytes();
        String base64File = Base64.getEncoder().encodeToString(fileBytes);

        String type = file.getContentType();

        Gson gson = new Gson();
        Map<String, Object> request = new HashMap<>();
        Map<String, String> argument = new HashMap<>();
        argument.put("type", type);
        argument.put("file", base64File);
        request.put("argument", argument);

        String jsonRequest = gson.toJson(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", accessKey);

        HttpEntity<String> entity = new HttpEntity<>(jsonRequest, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                openApiUrl,
                HttpMethod.POST,
                entity,
                String.class
        );


        return filterAndTranslateResponse(responseEntity.getBody());
    }

    private String filterAndTranslateResponse(String responseBody) {
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);

            List<Map<String, Object>> filteredData = new ArrayList<>();

            // confidence가 0.8 이상인 항목만 필터링 및 번역
            for (JsonNode item : rootNode.path("return_object").path("data")) {
                double confidence = item.path("confidence").asDouble();
                if (confidence >= 0.8) {
                    String keyword = item.path("class").asText();
                    String translatedKeyword = translateKeyword(keyword, "KO"); // 원하는 언어로 번역
                    filteredData.add(Map.of(
                            "keyword", translatedKeyword,
                            "confidence", confidence
                    ));
                }
            }

            return objectMapper.writeValueAsString(Map.of("result", filteredData));

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\":\"Error processing response\"}";
        }
    }

    public String translateKeyword(String keyword, String targetLang) {
        TranslationRequest request = new TranslationRequest(Collections.singletonList(keyword), targetLang);

//        logger.info(request.toString());

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "DeepL-Auth-Key " + authKey);

        HttpEntity<TranslationRequest> entity = new HttpEntity<>(request, headers);
        ResponseEntity<TranslationResponse> response = restTemplate.postForEntity(deeplApiUrl, entity, TranslationResponse.class);

//        logger.info(response.getBody().getTranslations().get(0).getText());


        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return response.getBody().getTranslations().get(0).getText();
        } else {
            throw new RuntimeException("번역 실패. 상태 코드: " + response.getStatusCode());
        }
    }
}
