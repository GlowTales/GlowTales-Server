package com.example.glowtales.dto.request;

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
    private String contents;
    private List<String> keywords;
}
