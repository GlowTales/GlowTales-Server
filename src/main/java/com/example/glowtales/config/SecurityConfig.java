package com.example.glowtales.config;

import com.example.glowtales.config.jwt.JwtFilter;
import com.example.glowtales.config.oauth.PrincipalOauth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsFilter corsFilter;
    private final JwtFilter jwtFilter;
    private final PrincipalOauth2UserService principalOauth2UserService;
    // 시큐리티 필터는 다른 어떤 필터보다 먼저 실행됨
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement((manager) -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //세션 로그인 안함
                .addFilter(corsFilter) // @CrossOrigin(인증 X), 시큐리티 필터에 등록 인증 (O)
                .formLogin(AbstractHttpConfigurer::disable) // 폼 로그인 안함
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/v1/members/oauth/**", "/swagger-ui/**", "/api/v1/members/token").permitAll()
                        .anyRequest().hasRole("USER")
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
