package com.example.glowtales.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class TestController {

//    @GetMapping("home")
//    public String home() {
//        return "<h1>home</h1>";
//    }
//
//    @PostMapping("token")
//    public String token() {
//        return "<h1>token</h1>";
//    }

    @GetMapping("/loginForm")
    public String loginForm() {
        System.out.println("loginForm");
        return "loginForm";
    }
}
