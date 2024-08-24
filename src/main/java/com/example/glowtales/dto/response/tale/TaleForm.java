package com.example.glowtales.dto.response.tale;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TaleForm {
    @NotBlank
    private String mood;
    private List<String> characters;
    @NotBlank
    private String contents;
    private List<String> keywords;
}
