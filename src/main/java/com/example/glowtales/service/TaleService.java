package com.example.glowtales.service;

import com.example.glowtales.domain.Member;
import com.example.glowtales.domain.Tale;
import com.example.glowtales.dto.response.HomeInfoResponseDto;
import com.example.glowtales.dto.response.TaleResponseDto;
import com.example.glowtales.dto.response.WordResponseDto;
import com.example.glowtales.repository.MemberRepository;
import com.example.glowtales.repository.TaleRepository;
import com.example.glowtales.repository.WordRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.gson.Gson;
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
public class TaleService {

    private final TaleRepository taleRepository;
    private final MemberRepository memberRepository;

    private final WordRepository wordRepository;

    @Autowired
    public TaleService(TaleRepository taleRepository, MemberRepository memberRepository,WordRepository wordRepository) {
        this.taleRepository = taleRepository;
        this.memberRepository=memberRepository;
        this.wordRepository=wordRepository;
    }

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
                .filter(tale -> tale.getLanguage_tale_list().stream()
                        .anyMatch(languageTale -> languageTale.getLanguage().getId() == 1 && languageTale.getIs_learned().getValue() == 2))
                .sorted(Comparator.comparing(Tale::getStudied_at).reversed());

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
                .filter(tale -> tale.getLanguage_tale_list().stream()
                        .anyMatch(languageTale -> languageTale.getLanguage().getId() == 1 && languageTale.getIs_learned().getValue() == 1))
                .sorted(Comparator.comparing(Tale::getStudied_at).reversed());

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
                        tale.getTale_word_list().stream()
                                .map(tw -> tw.getWord()) // Word 객체를 스트림으로 변환
                                .filter(word -> word != null && word.getOrigin_word() != null)
                                .map(WordResponseDto::new)
                );

        if (count > 0) {
            wordStream = wordStream.limit(count); // count가 0보다 큰 경우에만 limit 적용
        }

        return wordStream.collect(Collectors.toList()); // 리스트로 수집
    }

    //#010 사진에서 키워드 추출하기
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${api.open-api-url}")
    private String openApiUrl;

    @Value("${api.access-key}")
    private String accessKey;
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


        return filterResponse(responseEntity.getBody());
    }
    private String filterResponse(String responseBody) {
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);

            List<Map<String, Object>> filteredData = new ArrayList<>();

            // confidence가 0.8 이상인 항목만 필터링
            for (JsonNode item : rootNode.path("return_object").path("data")) {
                double confidence = item.path("confidence").asDouble();
                if (confidence >= 0.8) {
                    filteredData.add(Map.of(
                            "keyword", item.path("class").asText(),
                            "confidence", confidence
                    ));
                }
            }

            // 필터링된 결과를 JSON 문자열로 변환 및 반환
            return objectMapper.writeValueAsString(Map.of("result", filteredData));

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\":\"Error processing response\"}";
        }
    }
}
