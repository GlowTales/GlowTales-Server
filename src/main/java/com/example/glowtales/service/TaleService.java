package com.example.glowtales.service;

import com.example.glowtales.domain.Member;
import com.example.glowtales.domain.Tale;
import com.example.glowtales.dto.response.HomeInfoResponseDto;
import com.example.glowtales.dto.response.TaleResponseDto;
import com.example.glowtales.dto.response.WordResponseDto;
import com.example.glowtales.repository.MemberRepository;
import com.example.glowtales.repository.TaleRepository;
import com.example.glowtales.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TaleService {

    private final TaleRepository tale_repository;
    private final MemberRepository member_repository;

    private final WordRepository word_repository;

    @Autowired
    public TaleService(TaleRepository tale_repository, MemberRepository member_repository,WordRepository word_repository) {
        this.tale_repository = tale_repository;
        this.member_repository=member_repository;
        this.word_repository=word_repository;
    }

    //#001 전체 동화 상태창 불러오기
    public HomeInfoResponseDto getHomeInfoByMemberId(Long memberId) {
        Member member = member_repository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));
        return new HomeInfoResponseDto(member);
    }

    //#004 완료하지 않은 동화 미리보기 불러오기
    public List<TaleResponseDto> getUnlearnedTaleTop3ByMemberId(Long memberId) {
        List<Tale> tales = tale_repository.findByMemberId(memberId);
        return tales.stream()
                .filter(tale -> tale.getLanguage_tale_list().stream()
                        .anyMatch(languageTale -> languageTale.getLanguage().getId() == 1 && languageTale.getIs_learned().getValue() == 2))

                .sorted(Comparator.comparing(Tale::getStudied_at).reversed())
                .limit(3)
                .map(TaleResponseDto::new)
                .collect(Collectors.toList());
    }

    //#005 완료하지 않은 동화 모두 불러오기
    public List<TaleResponseDto> getUnlearnedTaleByMemberId(Long memberId,int count) {
        List<Tale> tales = tale_repository.findByMemberId(memberId);

        Stream<Tale> taleStream = tales.stream()
                .filter(tale -> tale.getLanguage_tale_list().stream()
                        .anyMatch(languageTale -> languageTale.getLanguage().getId() == 1 && languageTale.getIs_learned().getValue() == 2))
                .sorted(Comparator.comparing(Tale::getStudied_at).reversed());

        if (count > 0) {
            taleStream = taleStream.limit(count);
        }

        return taleStream
                .map(TaleResponseDto::new)
                .collect(Collectors.toList());
    }

    //#006 최근 학습한 동화 미리보기 불러오기
    public List<TaleResponseDto> getStudiedTaleTop3ByMemberId(Long memberId) {
        List<Tale> tales = tale_repository.findByMemberId(memberId);
        return tales.stream()
                .filter(tale -> tale.getLanguage_tale_list().stream()
                        .anyMatch(languageTale -> languageTale.getLanguage().getId() == 1 && languageTale.getIs_learned().getValue() == 1))
                .sorted(Comparator.comparing(Tale::getStudied_at).reversed())
                .limit(3)
                .map(TaleResponseDto::new)
                .collect(Collectors.toList());
    }

    //#007 최근 학습한 동화 모두 불러오기
    public List<TaleResponseDto> getStudiedTaleByMemberId(Long memberId, int count) {
        List<Tale> tales = tale_repository.findByMemberId(memberId);

        Stream<Tale> taleStream = tales.stream()
                .filter(tale -> tale.getLanguage_tale_list().stream()
                        .anyMatch(languageTale -> languageTale.getLanguage().getId() == 1 && languageTale.getIs_learned().getValue() == 1))
                .sorted(Comparator.comparing(Tale::getStudied_at).reversed());

        if (count > 0) {
            taleStream = taleStream.limit(count);
        }

        return taleStream
                .map(TaleResponseDto::new)
                .collect(Collectors.toList());
    }

    //#003 단어장 조회
    public List<WordResponseDto> getWordByMemberId(Long memberId, int count) {
        List<Tale> tales = tale_repository.findByMemberId(memberId);
        Stream<WordResponseDto> wordStream = tales.stream()
                .flatMap(tale ->
                        tale.getTale_word_list().stream()
                                .map(tw -> new WordResponseDto(tw.getWord()))
                );

        if (count > 0) {
            wordStream = wordStream.limit(count);
        }

        return wordStream.collect(Collectors.toList());
    }

}
