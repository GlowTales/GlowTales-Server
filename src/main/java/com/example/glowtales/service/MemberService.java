package com.example.glowtales.service;

import com.example.glowtales.config.jwt.JwtTokenProvider;
import com.example.glowtales.domain.Member;
import com.example.glowtales.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public Member findMemberByAccessToken(String accessToken) {
        return memberRepository.findByLoginId(jwtTokenProvider.getLoginIdFromAccessToken(accessToken));
    }
}
