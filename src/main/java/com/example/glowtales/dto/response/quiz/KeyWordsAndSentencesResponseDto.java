package com.example.glowtales.dto.response.quiz;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class KeyWordsAndSentencesResponseDto {

    private List<WordDto> words;
    private List<SentenceDto> sentences;

    @Getter
    @Setter
    public static class WordDto {
        private String word;
        private String mean;
    }

    @Getter
    @Setter
    public static class SentenceDto {
        private String sentence;
        private String mean;
    }
}