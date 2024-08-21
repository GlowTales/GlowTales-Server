package com.example.glowtales.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TaleRequest {
    @NotBlank
    private String detail;
    @NotBlank
    private String mood;
    private List<String> characters;
    @NotBlank
    private String contents;
    @NotBlank
    private List<String> keywords;
}
