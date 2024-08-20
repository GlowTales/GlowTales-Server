package com.example.glowtales.dto.response.tale;

import com.example.glowtales.domain.LanguageTale;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LanguageTaleTitleResponseDto {
    private String title;
    private Long id;

    @Builder
    public LanguageTaleTitleResponseDto(LanguageTale languageTale) {
        this.title = languageTale.getTitle();
        this.id=languageTale.getId();
    }
}
