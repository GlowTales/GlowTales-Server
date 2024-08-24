package com.example.glowtales.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizForm {
    @NotNull
    private Long languageTaleId;
    private String learningLevel;
}
