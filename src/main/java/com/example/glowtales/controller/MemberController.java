package com.example.glowtales.controller;

import com.example.glowtales.dto.response.auth.LoginResponse;
import com.example.glowtales.service.oauth.KakaoService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

    private final KakaoService kakaoService;

    @ResponseBody
    @GetMapping("/oauth/kakao/login")
    public ResponseEntity<LoginResponse> kakaoLogin(@RequestParam String code, HttpServletRequest request){
        try{
            // 현재 도메인 확인
            String currentDomain = request.getServerName();
            return ResponseEntity.ok(kakaoService.kakaoLogin(code, currentDomain));
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Item Not Found");
        }
    }
}
