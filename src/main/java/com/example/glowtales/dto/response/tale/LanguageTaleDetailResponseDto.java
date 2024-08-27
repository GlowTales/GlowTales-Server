package com.example.glowtales.dto.response.tale;

import com.example.glowtales.domain.LanguageTale;
import com.example.glowtales.domain.Tale;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

@Getter
@Setter
public class LanguageTaleDetailResponseDto {
    private Long taleId;
    private Long languageTaleId;
    private String title;
    private String language;
    private String story;
    private String createdAt;

    @Builder
    public LanguageTaleDetailResponseDto(Tale tale, Long languageId) {
        this.taleId = tale.getId();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        this.createdAt = tale.getCreatedAt().format(formatter);
        Optional<LanguageTale> firstLanguageTale = tale.getLanguageTaleList().stream()
                .filter(languageTale -> Objects.equals(languageTale.getLanguage().getId(), languageId))
                .findFirst();
        this.languageTaleId = firstLanguageTale.map(LanguageTale::getId).orElse(null);
        this.language = firstLanguageTale
                .map(languageTale -> languageTale.getLanguage().getLanguageName())
                .orElse("언어 정보 없음");

        this.title = firstLanguageTale
                .map(LanguageTale::getTitle)
                .orElse("동화가 존재하지 않습니다");
        this.story = firstLanguageTale
                .map(LanguageTale::getStory)
                .orElse("내용이 존재하지 않습니다");
    }
}
