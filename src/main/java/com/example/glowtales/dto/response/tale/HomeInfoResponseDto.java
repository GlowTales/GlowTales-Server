package com.example.glowtales.dto.response.tale;

import com.example.glowtales.domain.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        this.studiedTaleCount = (member.getTaleList() != null)
                ? member.getTaleList().stream()
                .flatMap(tale -> tale.getLanguageTaleList() != null ? tale.getLanguageTaleList().stream() : Stream.empty())
                .filter(languageTale -> languageTale.getIsLearned() != null && languageTale.getIsLearned().getValue() == 1)
                .count()
                : 0;

        this.studyCount = (member.getTaleList() != null)
                ? member.getTaleList().stream()
                .flatMap(tale -> tale.getLanguageTaleList() != null ? tale.getLanguageTaleList().stream() : Stream.empty())
                .mapToInt(languageTale -> languageTale != null ? languageTale.getCount() : 0)
                .sum()
                : 0;

        this.learningLanguageList = (member.getLearningLanguageList() != null)
                ? member.getLearningLanguageList().stream()
                .map(learningLanguage -> learningLanguage.getLanguage() != null ? learningLanguage.getLanguage().getLanguageName() : "Unknown")
                .collect(Collectors.toList())
                : List.of();
    }
}
