package com.example.glowtales.service;

import com.example.glowtales.domain.Tale;
import com.example.glowtales.dto.response.TaleResponseDto;
import com.example.glowtales.repository.TaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaleService {

    private final TaleRepository tale_repository;

    @Autowired
    public TaleService(TaleRepository tale_repository) {
        this.tale_repository = tale_repository;
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
    public List<TaleResponseDto> getUnlearnedTaleByMemberId(Long memberId) {
        List<Tale> tales = tale_repository.findByMemberId(memberId);
        return tales.stream()
                .filter(tale -> tale.getLanguage_tale_list().stream()
                        .anyMatch(languageTale -> languageTale.getLanguage().getId() == 1 && languageTale.getIs_learned().getValue() == 2))

                .sorted(Comparator.comparing(Tale::getStudied_at).reversed())
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
    public List<TaleResponseDto> getStudiedTaleByMemberId(Long memberId) {
        List<Tale> tales = tale_repository.findByMemberId(memberId);
        return tales.stream()
                .filter(tale -> tale.getLanguage_tale_list().stream()
                        .anyMatch(languageTale -> languageTale.getLanguage().getId() == 1 && languageTale.getIs_learned().getValue() == 1))

                .sorted(Comparator.comparing(Tale::getStudied_at).reversed())
                .map(TaleResponseDto::new)
                .collect(Collectors.toList());
    }


}
