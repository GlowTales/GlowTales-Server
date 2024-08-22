package com.example.glowtales.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizForm {
    @NotNull
    private Long taleId;
    @NotNull
    private Long languageId;
    private String learningLevel;
}
