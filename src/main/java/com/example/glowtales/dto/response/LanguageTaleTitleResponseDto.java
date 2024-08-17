package com.example.glowtales.dto.response;

import com.example.glowtales.domain.LanguageTale;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LanguageTaleTitleResponseDto {
    private String title;

    @Builder
    public LanguageTaleTitleResponseDto(LanguageTale languageTale) {
        this.title = languageTale.getTitle();
    }
}
