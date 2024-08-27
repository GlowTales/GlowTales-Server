package com.example.glowtales.service;

import com.example.glowtales.domain.Language;
import com.example.glowtales.domain.LanguageTale;
import com.example.glowtales.domain.Member;
import com.example.glowtales.domain.Tale;
import com.example.glowtales.dto.request.TranslationRequest;
import com.example.glowtales.dto.response.tale.*;
import com.example.glowtales.repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.gson.Gson;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
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
    private final PromptService promptService;
    private final LanguageRepository languageRepository;

    private static final Logger logger = LoggerFactory.getLogger(TaleService.class);

//    @Autowired
//    public TaleService(TaleRepository taleRepository, MemberRepository memberRepository,WordRepository wordRepository) {
//        this.taleRepository = taleRepository;
//        this.memberRepository=memberRepository;
//        this.wordRepository=wordRepository;
//    }

    //#001 전체 동화 상태창 불러오기
    public HomeInfoResponseDto getHomeInfoByMemberId(String accessToken) {
        if (accessToken == null) {
            throw new RuntimeException("accessToken이 null입니다");
        }
        Member member = memberService.findMemberByAccessToken(accessToken);
        if (member == null) {
            throw new RuntimeException("해당 아이디에 맞는 멤버가 없습니다.");
        }

        return new HomeInfoResponseDto(member);
    }


    //#005 완료하지 않은 동화 모두 불러오기
    public List<TaleResponseDto> getUnlearnedTaleByMemberId(String accessToken, int count, boolean koreaVersion) {
        if (accessToken == null) {
            throw new RuntimeException("accessToken이 null입니다");
        }
        Member member = memberService.findMemberByAccessToken(accessToken);
        if (member == null) {
            throw new RuntimeException("해당 아이디에 맞는 멤버가 없습니다.");
        }

        List<Tale> tales = taleRepository.findByMemberId(member.getId());

        Stream<TaleResponseDto> taleResponseDtoStream = tales.stream()
                .filter(tale -> koreaVersion
                        ? tale.getLanguageTaleList().stream().allMatch(languageTale -> languageTale.getIsLearned().getValue() == 1)
                        : true)
                .flatMap(tale -> tale.getLanguageTaleList().stream()
                        .filter(languageTale -> (koreaVersion
                                ? Objects.equals(languageTale.getLanguage().getLanguageName(), "Korean")
                                : true) && languageTale.getIsLearned().getValue() == 1)
                        .map(TaleResponseDto::new))
                .sorted(Comparator.comparing(TaleResponseDto::getTale_id));

        if (count > 0) {
            taleResponseDtoStream = taleResponseDtoStream.limit(count);
        }

        return taleResponseDtoStream.collect(Collectors.toList());

    }


    //#007 최근 학습한 동화 조회
    public List<TaleWithKoreanTitleResponseDto> getStudiedTaleByMemberId(String accessToken, int count, boolean koreaVersion) {
        if (accessToken == null) {
            throw new RuntimeException("accessToken이 null입니다");
        }
        Member member = memberService.findMemberByAccessToken(accessToken);
        if (member == null) {
            throw new RuntimeException("해당 아이디에 맞는 멤버가 없습니다.");
        }

        List<Tale> tales = taleRepository.findByMemberId(member.getId());
        Stream<TaleWithKoreanTitleResponseDto> taleResponseDtoStream = tales.stream()
                .flatMap(tale -> tale.getLanguageTaleList().stream()
                        .filter(languageTale -> (koreaVersion
                                ? Objects.equals(languageTale.getLanguage().getLanguageName(), "Korean")
                                : true) && languageTale.getIsLearned().getValue() == 0)
                        .map(languageTale -> {
                            // koreanTitle을 찾기 위해 languageId가 2인 객체 검색
                            String koreanTitle = tale.getLanguageTaleList().stream()
                                    .filter(lt -> lt.getLanguage().getId() == 2)
                                    .map(LanguageTale::getTitle)
                                    .findFirst()
                                    .orElse(null);
                            // TaleWithKoreanTitleResponseDto 생성
                            return new TaleWithKoreanTitleResponseDto(languageTale, koreanTitle);
                        }))
                .sorted(Comparator.comparing(TaleWithKoreanTitleResponseDto::getTale_id)); // Optional: Sort if necessary


        if (count > 0) {
            taleResponseDtoStream = taleResponseDtoStream.limit(count);
        }

        return taleResponseDtoStream.collect(Collectors.toList());
    }

    //#003 단어장 조회
    public List<WordResponseDto> getWordByMemberId(String accessToken, int count) {
        if (accessToken == null) {
            throw new RuntimeException("accessToken이 null입니다");
        }
        Member member = memberService.findMemberByAccessToken(accessToken);
        if (member == null) {
            throw new RuntimeException("해당 아이디에 맞는 멤버가 없습니다.");
        }

        List<Tale> tales = taleRepository.findByMemberId(member.getId()).stream()
                .sorted(Comparator.comparing(Tale::getCreatedAt).reversed())
                .collect(Collectors.toList());

        Stream<WordResponseDto> wordStream = tales.stream()
                .flatMap(tale -> tale.getLanguageTaleList().stream()
                        .flatMap(languageTale ->
                                languageTale.getLanguageTaleWordList().stream()
                                        .map(tw -> tw.getWord())
                                        .filter(word -> word != null && word.getOriginWord() != null)
                                        .map(WordResponseDto::new)
                        ));

        if (count > 0) {
            wordStream = wordStream.limit(count);
        }

        return wordStream.collect(Collectors.toList());
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

        if (responseEntity.getStatusCode().value() != 200) {
            String errorMessage = "키워드 추출 실패. 에러 코드: " + responseEntity.getStatusCode().value() + ", body: " + responseEntity.getBody();
            throw new RuntimeException(errorMessage);
        }


        return filterAndTranslateResponse(responseEntity.getBody());
    }

    private String filterAndTranslateResponse(String responseBody) {
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);

            List<Map<String, Object>> filteredData = new ArrayList<>();
            Set<String> seenKeywords = new HashSet<>(); // 중복 키워드 추적용

            // confidence가 0.8 이상인 항목만 필터링 및 번역
            for (JsonNode item : rootNode.path("return_object").path("data")) {
                double confidence = item.path("confidence").asDouble();
                if (confidence >= 0.8) {
                    String keyword = item.path("class").asText();
                    String translatedKeyword = translateKeyword(keyword, "KO"); // 원하는 언어로 번역

                    // 중복 키워드가 아닌 경우만 추가
                    if (!seenKeywords.contains(translatedKeyword)) {
                        seenKeywords.add(translatedKeyword);
                        filteredData.add(Map.of(
                                "keyword", translatedKeyword,
                                "confidence", confidence
                        ));
                    }
                }
            }

            return objectMapper.writeValueAsString(Map.of("result", filteredData));

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\":\"Error processing response\"}";
        }
    }

    public String translateKeyword(String keyword, String targetLang) {
        if (keyword.equals("cat")) {
            return "고양이";
        }
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
        if (accessToken == null) {
            throw new RuntimeException("accessToken이 null입니다");
        }
        Member member = memberService.findMemberByAccessToken(accessToken);
        if (member == null) {
            throw new RuntimeException("해당 아이디에 맞는 멤버가 없습니다.");
        }

        if (languageTaleId == null) {
            throw new RuntimeException("languageTaleId이 null입니다");
        }
        LanguageTale languageTale = languageTaleRepository.findById(languageTaleId)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이디인 languageTale이 없습니다. 아이디: " + languageTaleId));
        if (languageTale.getTale().getMember() != member) {
            throw new RuntimeException("본인의 동화에만 접근할 수 있습니다.");
        }
        return new LanguageTaleDetailResponseDto(languageTale.getTale(), languageTale.getLanguage().getId());
    }

    //#015 단일 동화 다국어로 조회
    public LanguageTaleDetailResponseDto getTaleBylanguageTaleIdLanguageId(String accessToken, Long taleId, Long languageId) {
        if (accessToken == null) {
            throw new RuntimeException("accessToken이 null입니다");
        }
        Member member = memberService.findMemberByAccessToken(accessToken);
        if (member == null) {
            throw new RuntimeException("해당 아이디에 맞는 멤버가 없습니다.");
        }

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
                .orElseThrow(() -> new NoSuchElementException("해당 언어로 생성된 languageTale 없습니다. tale 아이디: " + taleId + " 언어 아이디:" + languageId));

        if (foundLanguageTale.getTale().getMember() != member) {
            throw new RuntimeException("본인의 동화에만 접근할 수 있습니다.");
        }
        return new LanguageTaleDetailResponseDto(foundLanguageTale.getTale(), languageId);
    }

    // 동화 만들기
    @Transactional
    public PostTaleDto createLanguageTales(TaleForm taleForm, String accessToken) {
        // 1. accessToken -> member 반환
        Member member = memberService.findMemberByAccessToken(accessToken);

        // keywords 한 -> 영 변환
        List<String> keywords = new ArrayList<>();

        for (String mark : taleForm.getKeywords()) {
            keywords.add(wordRepository.findByOriginWordAndLanguage(wordRepository.findByMark(mark), languageRepository.findByLanguageName("English")).getMark());
        }

        taleForm.setKeywords(keywords);

        // 2. chatgpt한테 story 받아오기
        List<TaleDetailResponseDto> languageTales = promptService.createInitialTales(taleForm, member);
//        List<TaleDetailResponseDto> languageTales = promptService.test();
        // 3. tale 저장
        Tale tale = taleRepository.save(new Tale(member));
        // 4. languageTale 저장
        List<LanguageTaleAndLanguageDto> languageDtos = new ArrayList<>();

        if (languageTales.size() != 4) {
            throw new EntityNotFoundException("동화 생성에 실패하였습니다. 다시 시도해주세요.");
        }

        for (TaleDetailResponseDto languageTaleDto : languageTales) {
            Language language = languageRepository.findByLanguageName(languageTaleDto.getLanguageName());
            LanguageTale languageTale = languageTaleRepository.save(TaleDetailResponseDto.to(languageTaleDto, tale, language));
            languageDtos.add(LanguageTaleAndLanguageDto.builder().languageTaleId(languageTale.getId()).languageId(language.getId()).build());
        }

        return PostTaleDto.builder()
                .taleId(tale.getId())
                .createdAt(tale.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")))
                .languageTales(languageDtos)
                .build();
    }

    public CreatedRecentlyTaleDto getCreatedRecentlyTales(String accessToken, Integer count) {
        Member member = memberService.findMemberByAccessToken(accessToken);

        if (member == null) {
            throw new EntityNotFoundException("해당 회원이 존재하지 않습니다.");
        }

        List<TaleDto> tales = new ArrayList<>();

        for (Tale tale : taleRepository.findByMemberOrderByCreatedAtDesc(member)) {
            tales.add(TaleDto.builder()
                    .title(languageTaleRepository.findByLanguageAndTale(languageRepository.findByLanguageName("Korean"), tale).getTitle())
                    .createdAt(tale.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")))
                    .taleId(tale.getId())
                    .build());

            if (count != null) {
                if (tales.size() == count) {
                    break;
                }
            }
        }

        CreatedRecentlyTaleDto createdRecentlyTaleDto = new CreatedRecentlyTaleDto(tales);
        return createdRecentlyTaleDto;
    }
}
