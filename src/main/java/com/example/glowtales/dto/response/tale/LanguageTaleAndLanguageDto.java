package com.example.glowtales.dto.response.tale;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LanguageTaleAndLanguageDto {
    private Long languageTaleId;
    private Long languageId;
}
