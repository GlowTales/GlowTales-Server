package com.example.glowtales.controller;

import com.example.glowtales.domain.ResultCode;
import com.example.glowtales.dto.Result;
import com.example.glowtales.dto.request.TaleForm;
import com.example.glowtales.dto.response.tale.*;
import com.example.glowtales.service.LanguageTaleService;
import com.example.glowtales.service.MemberService;
import com.example.glowtales.service.PromptService;
import com.example.glowtales.service.TaleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tales")
@Tag(name = "Tale API", description = "API for managing tales")
public class TaleController {


//#011 동화의 퀴즈와 정답 불러오기
//#013 사용자 학습 언어와 수준 조회 GET
//#014 단일 동화 다국어로 조회 GET
//result 사용해서 응답 형식 통일하기


    private final TaleService taleService;
    private final MemberService memberService;
    private final ObjectMapper objectMapper;
    private final LanguageTaleService languageTaleService;

    private static final Logger logger = LoggerFactory.getLogger(TaleController.class);
    private final PromptService promptService;

    @Operation(summary = "#001 전체 동화 상태창 조회", description = "홈 화면에 나타나는 상태를 불러오는 API입니다.")
    @GetMapping("/")
    public ResponseEntity<HomeInfoResponseDto> getHomeInfoByMemberId(@RequestHeader(value = "Authorization", required = true) String accessToken) {

        HomeInfoResponseDto infos = taleService.getHomeInfoByMemberId(accessToken);
        return ResponseEntity.ok(infos);
    }

    @Operation(summary = "#005 완료하지 않은 동화 조회", description = "학습을 완료하지 않은 동화를 최신순으로 불러오는 API입니다. count는 limit을 의미하며, koreanVersion 은 홈/학습하기 화면에서 쓰이는 조건입니다.(홈/학습하기에는 한국어 버전의 동화만 보이며 미리보기로 화면에 보이는 동화의 수가 제한되어있음)")
    @GetMapping("/unlearned")
    public ResponseEntity<List<TaleResponseDto>> getUnlearnedTalesByMemberId(@RequestHeader(value = "Authorization", required = true) String accessToken, @RequestParam(name = "count", required = false) Integer count, @RequestParam Boolean koreanVersion) {
        logger.info("Count value: {}", count);
        if (count == null) {
            count = -1;//기본값 설정. 제한 없이 모든 동화를 불러옴
        }
        List<TaleResponseDto> posts = taleService.getUnlearnedTaleByMemberId(accessToken, count, koreanVersion);
        return ResponseEntity.ok(posts);
    }


    @Operation(summary = "#007 최근 학습한 동화 전체 조회", description = "최근 학습한 동화를 최신순으로 불러오는 API입니다.count는 limit을 의미하며, koreanVersion 은 홈/학습하기 화면에서 쓰이는 조건입니다.(홈/학습하기에는 한국어 버전의 동화만 보이며 미리보기로 화면에 보이는 동화의 수가 제한되어있음)")
    @GetMapping("/studied")
    public ResponseEntity<List<TaleResponseDto>> getStudiedTalesByMemberId(@RequestHeader(value = "Authorization", required = true) String accessToken, @RequestParam(name = "count", required = false) Integer count, @RequestParam Boolean koreanVersion) {
        logger.info("Count value: {}", count);
        if (count == null) {
            count = -1;//기본값 설정. 제한 없이 모든 동화를 불러옴
        }
        List<TaleResponseDto> posts = taleService.getStudiedTaleByMemberId(accessToken, count, koreanVersion);
        return ResponseEntity.ok(posts);
    }


    @Operation(summary = "#003 단어장 조회", description = "단어장을 조회하는 API입니다.")
    @GetMapping("/word")
    public ResponseEntity<List<WordResponseDto>> getWordByMemberId(@RequestHeader(value = "Authorization", required = true) String accessToken, @RequestParam(name = "count", required = false) Integer count) {
        if (count == null) {
            count = -1;//기본값 설정. 제한 없이 모든 동화를 불러옴
        }
        List<WordResponseDto> words = taleService.getWordByMemberId(accessToken, count);
        return ResponseEntity.ok(words);
    }

    @Operation(summary = "#010 사진에서 키워드 추출하기", description = "업로드한 사진에서 키워드를 추출하는 API입니다.")
    @PostMapping("/keyword")
    public ResponseEntity<KeywordResponseDto> handleFileUpload(@RequestHeader(value = "Authorization", required = true) String accessToken, @RequestParam("file") MultipartFile file) {
        try {
            String responseJson = taleService.getKeyword(file);
            KeywordResponseDto keywordResponseDto = objectMapper.readValue(responseJson, KeywordResponseDto.class);
            if (keywordResponseDto.getResult().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            return ResponseEntity.ok(keywordResponseDto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }

    @Operation(summary = "#014 단일 동화 조회", description = "languageTaleId를 통해 단일 동화를 조회하는 API입니다. 홈화면,학습하기 화면에서 단일 동화를 조회할 때 사용합니다.")
    @GetMapping("/detail")
    public ResponseEntity<LanguageTaleDetailResponseDto> getTaleBylanguageTaleId(@RequestHeader(value = "Authorization", required = true) String accessToken, @RequestParam Long languageTaleId) {
        try {
            LanguageTaleDetailResponseDto response = taleService.getTaleBylanguageTaleId(accessToken, languageTaleId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "#015 단일 동화 다국어로 조회", description = "TaleId와 languageId를 통해 단일 동화를 조회하는 API입니다. 동화 생성 후 언어를 바꿀때 사용합니다.1 = 영어 , 2 = 한국어 , 3 = 일본어 , 4 = 중국어")
    @GetMapping("/detail/lan")
    public ResponseEntity<LanguageTaleDetailResponseDto> getTaleBylanguageTaleIdLanguageId(@RequestHeader(value = "Authorization", required = true) String accessToken, @RequestParam Long taleId, @RequestParam Long languageId) {
        try {
            LanguageTaleDetailResponseDto response = taleService.getTaleBylanguageTaleIdLanguageId(accessToken, taleId, languageId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }


    @Operation(summary = "#008 동화 만들기", description = "동화를 만드는 API입니다.")
    @PostMapping("/")
    public Result createTales(
            @RequestBody @Valid TaleForm taleForm,
            @RequestHeader(value = "Authorization", required = false) String accessToken) {
        try {

            return new Result(ResultCode.SUCCESS, taleService.createLanguageTales(taleForm, accessToken));
        } catch (Exception e) {
            return new Result(ResultCode.FAIL, e.getMessage(), "400");
        }
    }

    @PutMapping("/")
    public Result updateLanguageTale(@RequestBody @Valid LanguageTaleDto languageTaleDto) {
        try {
            languageTaleService.updateIsLearned(languageTaleDto);
            return new Result(ResultCode.SUCCESS, null);
        } catch (Exception e) {
            return new Result(ResultCode.FAIL, e.getMessage(), "400");
        }
    }
}