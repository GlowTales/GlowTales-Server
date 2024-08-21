package com.example.glowtales.dto.response.member;

import com.example.glowtales.domain.Language;
import com.example.glowtales.domain.LearningLanguage;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class LearningLanguageResponseDto {
    private String languageName;
    private String learningLevel;

    @Builder
    public LearningLanguageResponseDto(Language language, LearningLanguage learningLanguage) {
        this.languageName = language.getLanguageName();
        this.learningLevel = learningLanguage.getLearningLevel();
    }

    public static List<LearningLanguageResponseDto> from(List<LearningLanguage> learningLanguages) {
        return learningLanguages.stream()
                .map(learningLanguage -> new LearningLanguageResponseDto(
                        learningLanguage.getLanguage(), learningLanguage))
                .collect(Collectors.toList());
    }

}
