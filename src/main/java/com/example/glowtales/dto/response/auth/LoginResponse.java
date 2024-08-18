package com.example.glowtales.dto.response.auth;

import com.example.glowtales.dto.jwt.AuthTokens;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginResponse {
    private String nickname;
    private String email;
    private AuthTokens token;

    public LoginResponse(String nickname, String email, AuthTokens token) {
        this.nickname = nickname;
        this.email = email;
        this.token = token;
    }
}