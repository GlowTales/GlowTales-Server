package com.example.glowtales.dto.response.tale;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class KeywordResponseDto {
    private List<KeywordConfidenceDto> result;

    @Getter
    @Setter
    public static class KeywordConfidenceDto {
        private String keyword;
        private double confidence;
    }
}
