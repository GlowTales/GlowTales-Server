package com.example.glowtales.dto.response;

import com.example.glowtales.domain.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class HomeInfoResponseDto {


    private int createdTaleCount;
    private Long studiedTaleCount;
    private int studyCount;
    private List<String> learningLanguageList;

    @Builder
    public HomeInfoResponseDto(Member member) {
        this.createdTaleCount = (member.getTaleList() != null) ? member.getTaleList().size() : 0;
        this.studiedTaleCount = member.getTaleList().stream()
                .flatMap(tale -> tale.getLanguageTaleList().stream())
                .filter(languageTale -> languageTale.getIsLearned().getValue() == 1)
                .count(); //
        this.studyCount= member.getTaleList().stream()
                .flatMap(tale -> tale.getLanguageTaleList().stream())
                .mapToInt(LanguageTale::getCount)
                .sum();

        this.learningLanguageList=member.getLearningLanguageList().stream()
                .map(LearningLanguage::getLanguage)
                .map(Language::getLanguageName)
                .collect(Collectors.toList());

    }
}