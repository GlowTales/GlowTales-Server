package com.example.glowtales.dto.response.tale;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class PostTaleDto {
    private Long taleId;
    private List<LanguageTaleAndLanguageDto> languageTales;
    private String createdAt;
}
