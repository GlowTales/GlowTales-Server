package com.example.glowtales.dto.response.tale;

import com.example.glowtales.domain.LanguageTale;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TaleResponseDto {
    private Long tale_id;
    private LocalDateTime createdAt;
    private LanguageTaleTitleResponseDto languageTale;

    private Integer firstQuizCount;

    @Builder
    public TaleResponseDto(LanguageTale languageTale) {
        this.tale_id = languageTale.getTale().getId();
        this.createdAt=languageTale.getTale().getCreatedAt();
        this.languageTale = new LanguageTaleTitleResponseDto(languageTale);
        this.firstQuizCount=languageTale.getFistQuizCount();

    }
}