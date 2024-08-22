package com.example.glowtales.dto.language;

import com.example.glowtales.domain.Language;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LanguageDto {
    private Long languageId;
    private String languageName;

    public static LanguageDto of(Language language) {
        return new LanguageDto(language.getId(), language.getLanguageName());
    }
}
