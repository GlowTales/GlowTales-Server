package com.example.glowtales.service;

import com.example.glowtales.config.jwt.JwtTokenProvider;
import com.example.glowtales.domain.LearningLanguage;
import com.example.glowtales.domain.Member;
import com.example.glowtales.domain.YesOrNo;
import com.example.glowtales.dto.request.MemberForm;
import com.example.glowtales.dto.response.member.LearningLanguageResponseDto;
import com.example.glowtales.repository.LanguageRepository;
import com.example.glowtales.repository.LearningLanguageRepository;
import com.example.glowtales.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final LanguageRepository languageRepository;
    private final LearningLanguageRepository learningLanguageRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public Member findMemberByAccessToken(String accessToken) {
        return memberRepository.findByLoginId(jwtTokenProvider.getLoginIdFromAccessToken(accessToken.substring(7)));
    }

    @Transactional
    public void updateLearningLanguageAndLearningLevelAndAge(MemberForm memberForm, String accessToken) {
        Member member = findMemberByAccessToken(accessToken);

        if (member == null) {
            throw new EntityNotFoundException("member not found");
        }

        // member 추가 정보 저장
        member.updateAge(memberForm.getAge());

        // learningLanguage 저장
       learningLanguageRepository.save(
               LearningLanguage.builder()
                .language(languageRepository.findById(memberForm.getLanguageId()).orElseThrow(() -> new NoSuchElementException("일치하는 언어가 존재하지 않습니다.")))
                .member(member)
                .learningLevel(memberForm.getLearningLevel())
                .build()
       );
    }

    public String getAccessToken() {
        return jwtTokenProvider.accessTokenGenerate("2", new Date((new Date()).getTime() + 1000 * 60 * 60));
    }

//    public List<LearningLanguageResponseDto> getLanguageLearningDataByMemberId(String accessToken) {
//        if (accessToken==null){
//            throw new RuntimeException("accessToken이 null입니다");}
//        Member member=findMemberByAccessToken(accessToken);
//        if (member==null){
//            throw new RuntimeException("해당 아이디에 맞는 멤버가 없습니다.");}
//        List<LearningLanguage> learningLanguages = learningLanguageRepository.findByMemberId(member.getId());
//
//        return LearningLanguageResponseDto.from(learningLanguages);
//    }

    public LearningLanguageResponseDto getLearningLevel(String accessToken, Long languageId) {
        Member member = findMemberByAccessToken(accessToken);
        if (member == null) {
            throw new EntityNotFoundException("해당 회원이 존재하지 않습니다.");
        }
        LearningLanguage learningLanguage = learningLanguageRepository.findByMemberAndLanguage(member, languageRepository.findById(languageId).orElseThrow(() -> new NoSuchElementException("해당 언어가 존재하지 않습니다.")));

        if (learningLanguage == null) {
            return LearningLanguageResponseDto.builder()
                    .isLearned(YesOrNo.NO)
                    .build();
        } else {
            return LearningLanguageResponseDto.builder()
                    .isLearned(YesOrNo.YES)
                    .learningLevel(learningLanguage.getLearningLevel())
                    .build();
        }

    }
}
