package com.example.glowtales.dto.response;

import com.example.glowtales.domain.Word;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WordResponseDto {

    private String word;
    private String origin_word;

    @Builder
    public WordResponseDto(Word word) {
        this.word = word.getMark();
        this.origin_word = word.getOrigin_word().getMark();
    }
}
