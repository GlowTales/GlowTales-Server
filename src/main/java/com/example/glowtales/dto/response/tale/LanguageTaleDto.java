package com.example.glowtales.dto.response.tale;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LanguageTaleDto {
    @NotNull
    private Long languageTaleId;
    private Integer answerCounts;
}
