package com.example.glowtales.dto.response.quiz;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class KeywordsAndKeySentencesDto {
    private List<KeyResponseDto> keywords;
    private List<KeyResponseDto> keySentences;
}
