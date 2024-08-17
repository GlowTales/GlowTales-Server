package com.example.glowtales.config;

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
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsFilter corsFilter;
    private final PrincipalOauth2UserService principalOauth2UserService;
    // 시큐리티 필터는 다른 어떤 필터보다 먼저 실행됨
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
//        http.addFilterBefore(new MemberFilter3(), SecurityContextPersistenceFilter.class);
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement((manager) -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //세션 로그인 안함
                .addFilter(corsFilter) // @CrossOrigin(인증 X), 시큐리티 필터에 등록 인증 (O)
                .formLogin(AbstractHttpConfigurer::disable) // 폼 로그인 안함
                .httpBasic(AbstractHttpConfigurer::disable)
//                .addFilter(new JwtAuthenticationFilter()) // AuthenticationManager를 parameter로 던져줘야함
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/v1/member/user/**").hasRole("USER") // /user/** 경로는 인증된 사용자만 접근 가능
                        .requestMatchers("/api/v1/member/admin/**").hasRole("ADMIN") // /admin/** 경로는 ROLE_ADMIN 권한을 가진 사용자만 접근 가능
                        .anyRequest().permitAll() // 그 외의 모든 요청은 인증 없이 접근 가능
                )
                .oauth2Login(oauth2Login -> {
                    oauth2Login.loginPage("/loginForm");
                    oauth2Login.userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig.userService(principalOauth2UserService));
                })
        ;
        return http.build();
    }
}
