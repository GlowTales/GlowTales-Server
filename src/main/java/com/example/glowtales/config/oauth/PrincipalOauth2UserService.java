package com.example.glowtales.config.oauth;

import com.example.glowtales.config.auth.PrincipalDetails;
import com.example.glowtales.domain.Member;
import com.example.glowtales.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        //registerationId로 어떤 OAuth로 로그인 했는지 확인가능
        System.out.println("getClientRegistration: " + oAuth2UserRequest.getClientRegistration());
        // 사실 엑세스토큰이 없어도 아래에서 getAttributes에서 사용자 정보를 모두 받아올 수 있어서 딱히 필요하지 않음
        System.out.println("getAccessToken: " + oAuth2UserRequest.getAccessToken().getTokenValue());

        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
        // 구글 로그인 버튼 클릭 -> 구글로그인 창 -> 로그인 완료 -> code 리턴(OAuth-Client 라이브러리) -> Access Token 요청
        // userRequest 정보 -> loadUser 함수 호출 -> 구글로부터 회원프로필을 받아줌.
        System.out.println("getAttributes: " + oAuth2User.getAttributes());

        //회원가입을 강제로 진행해볼 예정
        String provider = oAuth2UserRequest.getClientRegistration().getClientId();//google
        String providerId = oAuth2User.getAttribute("sub").toString();
        String username = provider + "_" + providerId; //google_xxxxxxxxxxx -> username 충돌 방지
//        String password = bCryptPasswordEncoder.encode("겟인데어");
        String email = oAuth2User.getAttribute("email").toString();
        String role = "USER";

        Member member = memberRepository.findByEmail(email);

        if (member == null) {
            System.out.println("최초 로그인");
            member = Member.builder()
                    .name(username)
                    .email(email)
                    .roles(role)
                    .provider(provider)
                    .build();

            memberRepository.save(member);
        } else {
            System.out.println("이미 회원가입이 완료됨");
        }

        return new PrincipalDetails(member, oAuth2User.getAttributes()); //Authentication 객체에 들어감 -> user와 attributes
    }
}
