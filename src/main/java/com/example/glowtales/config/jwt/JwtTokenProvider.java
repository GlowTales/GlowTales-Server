package com.example.glowtales.config.jwt;

import com.example.glowtales.config.auth.PrincipalDetails;
import com.example.glowtales.config.auth.PrincipalDetailsService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {
    private final Key key;
    private final PrincipalDetailsService principalDetailsService;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey, PrincipalDetailsService principalDetailsService) {
        this.principalDetailsService = principalDetailsService;
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public UsernamePasswordAuthenticationToken getAuthentication(String token) {
        String loginId = getLoginIdFromAccessToken(token);
        PrincipalDetails principalDetails = (PrincipalDetails) principalDetailsService.loadUserByUsername(loginId);

        // 만약 권한이 한 가지로 고정이라면, 예를 들어 ROLE_USER
        return new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());
    }

    public String accessTokenGenerate(String subject, Date expiredAt) {
        return Jwts.builder()
                .setSubject(subject)    //uid
                .setExpiration(expiredAt)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String refreshTokenGenerate(Date expiredAt) {
        return Jwts.builder()
                .setExpiration(expiredAt)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String getLoginIdFromAccessToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            // 토큰의 유효성 검사를 위해 파싱 시도
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true; // 파싱이 성공하면 토큰은 유효함
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false; // 위 예외 중 하나라도 발생하면 토큰은 유효하지 않음
    }
}