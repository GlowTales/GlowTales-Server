package com.example.glowtales.service;

import com.example.glowtales.domain.LanguageTale;
import com.example.glowtales.domain.Member;
import com.example.glowtales.domain.Tale;
import com.example.glowtales.domain.Word;
import com.example.glowtales.dto.request.TranslationRequest;
import com.example.glowtales.dto.response.tale.*;
import com.example.glowtales.repository.LanguageTaleRepository;
import com.example.glowtales.repository.MemberRepository;
import com.example.glowtales.repository.TaleRepository;
import com.example.glowtales.repository.WordRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
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
    private final MemberService memberService;
    private final LanguageTaleRepository languageTaleRepository;

    private static final Logger logger = LoggerFactory.getLogger(TaleService.class);

//    @Autowired
//    public TaleService(TaleRepository taleRepository, MemberRepository memberRepository,WordRepository wordRepository) {
//        this.taleRepository = taleRepository;
//        this.memberRepository=memberRepository;
//        this.wordRepository=wordRepository;
//    }

    //#001 전체 동화 상태창 불러오기
    public HomeInfoResponseDto getHomeInfoByMemberId(String accessToken) {
        if (accessToken==null){
            throw new RuntimeException("accessToken이 null입니다");}
        Member member=memberService.findMemberByAccessToken(accessToken);
        if (member==null){
            throw new RuntimeException("해당 아이디에 맞는 멤버가 없습니다.");}
//        Member member = memberRepository.findById(memberId)
//                .orElseThrow(() -> new RuntimeException("해당 아이디에 맞는 멤버가 없습니다. 아이디: " + memberId));
        return new HomeInfoResponseDto(member);
    }



    //#005 완료하지 않은 동화 모두 불러오기
    public List<TaleResponseDto> getUnlearnedTaleByMemberId(String accessToken, int count) {
        if (accessToken==null){
            throw new RuntimeException("accessToken이 null입니다");}
        Member member=memberService.findMemberByAccessToken(accessToken);
        if (member==null){
            throw new RuntimeException("해당 아이디에 맞는 멤버가 없습니다.");}

        List<Tale> tales = taleRepository.findByMemberId(member.getId());

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


    //#007 최근 학습한 동화 모두 불러오기
    public List<TaleResponseDto> getStudiedTaleByMemberId(String accessToken, int count) {
        if (accessToken==null){
            throw new RuntimeException("accessToken이 null입니다");}
        Member member=memberService.findMemberByAccessToken(accessToken);
        if (member==null){
            throw new RuntimeException("해당 아이디에 맞는 멤버가 없습니다.");}

        List<Tale> tales = taleRepository.findByMemberId(member.getId());

        Stream<Tale> taleStream = tales.stream()
                .filter(tale -> tale.getLanguageTaleList().stream()
                        .anyMatch(languageTale -> languageTale.getLanguage().getId() == 1 && languageTale.getIsLearned().getValue() == 0))
                .sorted(Comparator.comparing(Tale::getStudiedAt).reversed());

        if (count > 0) {
            taleStream = taleStream.limit(count);
        }

        return taleStream
                .map(TaleResponseDto::new)
                .collect(Collectors.toList());
    }

    //#003 단어장 조회
    public List<WordResponseDto> getWordByMemberId(String accessToken, int count) {
        if (accessToken==null){
            throw new RuntimeException("accessToken이 null입니다");}
        Member member=memberService.findMemberByAccessToken(accessToken);
        if (member==null){
            throw new RuntimeException("해당 아이디에 맞는 멤버가 없습니다.");}

        List<Tale> tales = taleRepository.findByMemberId(member.getId());
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
    private String deeplApiUrl;

    @Value("${api.deepL.access-key}")
    private String authKey;

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

    //#014 단일 동화 조회
    public LanguageTaleDetailResponseDto getTaleBylanguageTaleId(String accessToken, Long languageTaleId) {
        if (accessToken==null){
            throw new RuntimeException("accessToken이 null입니다");}
        Member member=memberService.findMemberByAccessToken(accessToken);
        if (member==null){
            throw new RuntimeException("해당 아이디에 맞는 멤버가 없습니다.");}

        if (languageTaleId == null) {
            throw new RuntimeException("languageTaleId이 null입니다");
        }
        LanguageTale languageTale = languageTaleRepository.findById(languageTaleId)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이디인 languageTale이 없습니다. 아이디: " + languageTaleId));
        if(languageTale.getTale().getMember() != member){
            throw new RuntimeException("본인의 동화에만 접근할 수 있습니다.");
        }
        return new LanguageTaleDetailResponseDto(languageTale.getTale(),languageTale.getLanguage().getId());
    }

    //#015 단일 동화 다국어로 조회
    public LanguageTaleDetailResponseDto getTaleBylanguageTaleIdLanguageId(String accessToken, Long taleId,Long languageId) {
        if (accessToken==null){
            throw new RuntimeException("accessToken이 null입니다");}
        Member member=memberService.findMemberByAccessToken(accessToken);
        if (member==null){
            throw new RuntimeException("해당 아이디에 맞는 멤버가 없습니다.");}

        if (taleId == null) {
            throw new RuntimeException("TaleId가 null입니다");
        }
        if (languageId == null) {
            throw new RuntimeException("languageId가 null입니다");
        }

        Tale tale = taleRepository.findById(taleId)
                .orElseThrow(() -> new NoSuchElementException("해당 아이디인 tale이 없습니다. 아이디: " + taleId));
        LanguageTale foundLanguageTale = tale.getLanguageTaleList().stream()
                .filter(lt -> lt.getLanguage().getId() == languageId)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당 언어로 생성된 languageTale 없습니다. tale 아이디: " + taleId+" 언어 아이디:"+languageId));

        if(foundLanguageTale.getTale().getMember() != member){
            throw new RuntimeException("본인의 동화에만 접근할 수 있습니다.");
        }
        return new LanguageTaleDetailResponseDto(foundLanguageTale.getTale(),languageId);
    }

}
