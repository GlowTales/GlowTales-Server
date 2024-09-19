package com.example.glowtales.controller;

import com.example.glowtales.domain.ResultCode;
import com.example.glowtales.dto.Result;
import com.example.glowtales.dto.response.tale.*;
import com.example.glowtales.service.LanguageTaleService;
import com.example.glowtales.service.TaleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tales")
@Tag(name = "Tale API", description = "API for managing tales")
public class TaleController {

    private final TaleService taleService;
    private final ObjectMapper objectMapper;
    private final LanguageTaleService languageTaleService;

    private static final Logger logger = LoggerFactory.getLogger(TaleController.class);

    @Operation(summary = "#001 전체 동화 상태창 조회", description = "홈 화면에 나타나는 상태를 불러오는 API입니다.")
    @GetMapping("/status")
    public Result<HomeInfoResponseDto> getHomeInfoByMemberId(@RequestHeader(value = "Authorization", required = true) String accessToken) {
        try {
            HomeInfoResponseDto infos = taleService.getHomeInfoByMemberId(accessToken);
            return new Result(ResultCode.SUCCESS, infos);
        } catch (Exception e) {
            return new Result(ResultCode.FAIL, e.getMessage(), "400");
        }

    }

    @Operation(summary = "#005 완료하지 않은 동화 조회", description = "학습을 완료하지 않은 동화를 최신순으로 불러오는 API입니다. count는 limit을 의미하며, koreanVersion 은 홈/학습하기 화면에서 쓰이는 조건입니다.(홈/학습하기에는 한국어 버전의 동화만 보이며 미리보기로 화면에 보이는 동화의 수가 제한되어있음)")
    @GetMapping("/unlearned")
    public Result<List<TaleResponseDto>> getUnlearnedTalesByMemberId(@RequestHeader(value = "Authorization", required = true) String accessToken, @RequestParam(name = "count", required = false) Integer count, @RequestParam Boolean koreanVersion) {
        try {
            if (count == null) {
                count = -1;//기본값 설정. 제한 없이 모든 동화를 불러옴
            }
            List<TaleResponseDto> posts = taleService.getUnlearnedTaleByMemberId(accessToken, count, koreanVersion);
            return new Result(ResultCode.SUCCESS, posts);
        } catch (Exception e) {
            return new Result(ResultCode.FAIL, e.getMessage(), "400");
        }

    }


    @Operation(summary = "#007 최근 학습한 동화 전체 조회", description = "최근 학습한 동화를 최신순으로 불러오는 API입니다.count는 limit을 의미하며, koreanVersion 은 홈/학습하기 화면에서 쓰이는 조건입니다.(홈/학습하기에는 한국어 버전의 동화만 보이며 미리보기로 화면에 보이는 동화의 수가 제한되어있음)")
    @GetMapping("/studied")
    public Result<List<TaleWithKoreanTitleResponseDto>> getStudiedTalesByMemberId(@RequestHeader(value = "Authorization", required = true) String accessToken, @RequestParam(name = "count", required = false) Integer count, @RequestParam Boolean koreanVersion) {
        try {
            if (count == null) {
                count = -1;//기본값 설정. 제한 없이 모든 동화를 불러옴
            }
            List<TaleWithKoreanTitleResponseDto> posts = taleService.getStudiedTaleByMemberId(accessToken, count, koreanVersion);
            return new Result(ResultCode.SUCCESS, posts);
        } catch (Exception e) {
            return new Result(ResultCode.FAIL, e.getMessage(), "400");
        }

    }


    @Operation(summary = "#003 단어장 조회", description = "단어장을 조회하는 API입니다. v2")
    @GetMapping("/word")
    public Result<List<WordResponseDto>> getWordByMemberIdV2(@RequestHeader(value = "Authorization", required = true) String accessToken, @RequestParam(name = "count", required = false) Integer count) {
        try {
            if (count == null) {
                count = -1;//기본값 설정. 제한 없이 모든 동화를 불러옴
            }
            List<WordResponseDto> words = taleService.getWordByMemberId(accessToken, count);
            return new Result(ResultCode.SUCCESS, words);
        } catch (Exception e) {
            return new Result(ResultCode.FAIL, e.getMessage(), "400");
        }
    }

    @Operation(summary = "#010 사진에서 키워드 추출하기", description = "업로드한 사진에서 키워드를 추출하는 API입니다.")
    @PostMapping("/keyword")
    public Result<KeywordResponseDto> handleFileUpload(@RequestHeader(value = "Authorization", required = true) String accessToken, @RequestParam("file") MultipartFile file) {
        try {
            String responseJson = taleService.getKeyword(file);
            KeywordResponseDto keyword = objectMapper.readValue(responseJson, KeywordResponseDto.class);
            if (keyword.getResult().isEmpty()) {
                return new Result(ResultCode.FAIL, "키워드 추출에 실패하였습니다. 다른 사진을 넣어주세요", "400");
            }
            return new Result(ResultCode.SUCCESS, keyword);
        } catch (Exception e) {
            return new Result(ResultCode.FAIL, e.getMessage(), "400");
        }

    }

    @Operation(summary = "#014 단일 동화 조회", description = "languageTaleId를 통해 단일 동화를 조회하는 API입니다. 홈화면,학습하기 화면에서 단일 동화를 조회할 때 사용합니다.")
    @GetMapping("/detail")
    public Result<LanguageTaleDetailResponseDto> getTaleBylanguageTaleId(@RequestHeader(value = "Authorization", required = true) String accessToken, @RequestParam Long languageTaleId) {
        try {
            LanguageTaleDetailResponseDto tale = taleService.getTaleBylanguageTaleId(accessToken, languageTaleId);
            return new Result(ResultCode.SUCCESS, tale);
        } catch (Exception e) {
            return new Result(ResultCode.FAIL, e.getMessage(), "400");
        }
    }

    @Operation(summary = "#015 단일 동화 다국어로 조회", description = "TaleId와 languageId를 통해 단일 동화를 조회하는 API입니다. 동화 생성 후 언어를 바꿀때 사용합니다.1 = 영어 , 2 = 한국어 , 3 = 일본어 , 4 = 중국어")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공적으로 동화 조회"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),
            @ApiResponse(responseCode = "403", description = "권한이 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),
            @ApiResponse(responseCode = "404", description = "언어 또는 동화가 존재하지 않음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class)))
    })
    @GetMapping("/detail/lan")
    public Result<LanguageTaleDetailResponseDto> getTaleBylanguageTaleIdLanguageId(@RequestHeader(value = "Authorization", required = true) String accessToken, @RequestParam Long taleId, @RequestParam Long languageId) {
        LanguageTaleDetailResponseDto tale = taleService.getTaleBylanguageTaleIdLanguageId(accessToken, taleId, languageId);
        return new Result(ResultCode.SUCCESS, tale);
    }

    @Operation(summary = "최근 생성된 동화 조회")
    @GetMapping("/recently")
    public Result getCreatedRecentlyTales(
            @RequestHeader(value = "Authorization", required = true) String accessToken,
            @RequestParam(required = false) Integer count
    ) {
        try {
            return new Result(ResultCode.SUCCESS, taleService.getCreatedRecentlyTales(accessToken, count));
        } catch (Exception e) {
            return new Result(ResultCode.FAIL, e.getMessage(), "400");
        }
    }


    @Operation(summary = "#008 동화 만들기", description = "동화를 만드는 API입니다.")
    @PostMapping("/")
    public Result<PostTaleDto> createTales(
            @RequestBody @Valid TaleForm taleForm,
            @RequestHeader(value = "Authorization", required = false) String accessToken) {
        try {

            return new Result(ResultCode.SUCCESS, taleService.createLanguageTales(taleForm, accessToken));
        } catch (Exception e) {
            return new Result(ResultCode.FAIL, e.getMessage(), "400");
        }
    }


    @GetMapping("/learned")
    public Result getAllQuizInfo(
            @RequestParam Long taleId,
            @RequestHeader(value = "Authorization") String accessToken) {
        try {
            return new Result(ResultCode.SUCCESS, languageTaleService.getAllQuizInfo(accessToken, taleId));
        } catch (Exception e) {
            return new Result(ResultCode.FAIL, e.getMessage(), "400");
        }
    }
}