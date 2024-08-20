package com.example.glowtales.dto.response.tale;

import com.example.glowtales.domain.Tale;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaleResponseDto {
    private Long tale_id;
    private LanguageTaleTitleResponseDto languageTale;

    @Builder
    public TaleResponseDto(Tale tale) {
        this.tale_id = tale.getId();
        this.languageTale = tale.getLanguageTaleList().stream()
//                .filter(languageTale -> isKorean(languageTale.getLanguage().getId()))
                .findFirst()
                .map(LanguageTaleTitleResponseDto::new)
                .orElse(null);
    }

//    private boolean isKorean(Long language_id) {
//        return language_id == 1;
//    }
}