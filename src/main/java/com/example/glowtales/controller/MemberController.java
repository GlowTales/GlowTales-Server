package com.example.glowtales.controller;

import com.example.glowtales.dto.response.auth.LoginResponse;
import com.example.glowtales.service.oauth.KakaoService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // 카카오 로그인 로직
    @ResponseBody
    @GetMapping("/oauth/kakao/login")
    public ResponseEntity<LoginResponse> kakaoLogin(@RequestParam String code, HttpServletRequest request){
        try{
            logger.info("domain: " + request.getServerName());
            return ResponseEntity.ok(kakaoService.kakaoLogin(code, request.getServerName()));
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Item Not Found");
        }
    }

//    @PutMapping("")
//    public ResponseEntity<> updateMember() {
//
//    }
}
