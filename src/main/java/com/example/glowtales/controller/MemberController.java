package com.example.glowtales.controller;

import com.example.glowtales.domain.ResultCode;
import com.example.glowtales.dto.Result;
import com.example.glowtales.dto.request.MemberForm;
import com.example.glowtales.dto.response.member.LearningLanguageResponseDto;
import com.example.glowtales.service.MemberService;
import com.example.glowtales.service.oauth.KakaoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

    private final KakaoService kakaoService;
    private final MemberService memberService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // 카카오 로그인 로직
    @ResponseBody
    @GetMapping("/oauth/kakao/login")
    public Result kakaoLogin(@RequestParam String code, HttpServletRequest request) {
        try {
            logger.info("domain: " + request.getServerName());
            return new Result(ResultCode.SUCCESS, kakaoService.kakaoLogin(code, request.getServerName()));
        } catch (NoSuchElementException e) {
            return new Result(ResultCode.FAIL, e.getMessage());
        }
    }

    @PutMapping("")
    public Result updateMember(
            @RequestBody @Valid MemberForm memberForm,
            @RequestHeader(value = "Authorization", required = false) String accessToken) {
        try {
            memberService.updateLearningLanguageAndLearningLevelAndAge(memberForm, accessToken);
            return new Result(ResultCode.SUCCESS, null);
        } catch (EntityNotFoundException | NoSuchElementException e) {
            return new Result(ResultCode.FAIL, e.getMessage(), "400");
        } catch (Exception e) {
            return new Result(ResultCode.FAIL, e.getMessage(), "400");
        }
    }

    @GetMapping("/token/hy")
    public Result getTokenForHayeong() {
        return new Result(ResultCode.SUCCESS, memberService.getAccessTokenForHayeong());
    }

    @GetMapping("/token/es")
    public Result getTokenForEunsu() {
        return new Result(ResultCode.SUCCESS, memberService.getAccessTokenForEunsu());
    }


    @Operation(summary = "#013 사용자 학습 언어와 수준 조회", description = "사용자가 학습 중인 언어와 수준을 조회하는 API입니다.")
    @GetMapping("/learningLevel")
    public Result<LearningLanguageResponseDto> getLearningLevel(
            @RequestHeader(value = "Authorization") String accessToken,
            @RequestParam Long languageId
    ) {
        try {
            return new Result(ResultCode.SUCCESS, memberService.getLearningLevel(accessToken, languageId));
        } catch (Exception e) {
            return new Result(ResultCode.FAIL, e.getMessage(), "400");
        }
    }
}
