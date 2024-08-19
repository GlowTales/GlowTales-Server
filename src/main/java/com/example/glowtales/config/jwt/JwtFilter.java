package com.example.glowtales.config.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;


@Component
@RequiredArgsConstructor
public class JwtFilter extends HttpFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private static final List<String> EXCLUDE_URLS = List.of("/api/v1/members/oauth/kakao/login", "/swagger-ui/index.html");


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 필터 초기화 시 필요한 작업
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        String token = request.getHeader("Authorization"); // Authorization 헤더에서 토큰을 가져옴

        String requestURI = request.getRequestURI();

        // 특정 경로에 대해서는 필터를 우회
        if (EXCLUDE_URLS.stream().anyMatch(requestURI::startsWith)) {
            System.out.println("우회됨");
            chain.doFilter(request, response);
            return;
        }

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // "Bearer " 이후의 토큰만 추출

            if (jwtTokenProvider.validateToken(token)) {
                // 유효한 토큰인 경우 요청을 계속 진행
                chain.doFilter(request, response);
                return;
            }
        }

        // 유효하지 않은 토큰이거나 토큰이 없는 경우 401 Unauthorized 응답
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Invalid or missing JWT token");
    }

    @Override
    public void destroy() {
        // 필터 종료 시 필요한 작업
    }
}
