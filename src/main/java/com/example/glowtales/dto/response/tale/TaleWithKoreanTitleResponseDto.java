package com.example.glowtales.dto.response.tale;

import com.example.glowtales.domain.LanguageTale;
import com.example.glowtales.domain.Tale;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class TaleWithKoreanTitleResponseDto {
    private Long tale_id;
    private String createdAt;
    private LanguageTaleTitleResponseDto languageTale;
    private Long languageId;
    private Integer firstQuizCount;
    private String koreanTitle;

    @Builder
    public TaleWithKoreanTitleResponseDto(LanguageTale languageTale,String koreanTitle) {
        Tale tale=languageTale.getTale();
        this.tale_id = tale.getId();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        this.createdAt = tale.getCreatedAt().format(formatter);
        this.languageTale = new LanguageTaleTitleResponseDto(languageTale);
        this.firstQuizCount=languageTale.getFirstQuizCount();
        this.languageId=languageTale.getLanguage().getId();
        this.koreanTitle=koreanTitle;

    }
}