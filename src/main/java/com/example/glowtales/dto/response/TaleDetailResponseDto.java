package com.example.glowtales.dto.response;

import com.example.glowtales.domain.LanguageTale;
import com.example.glowtales.domain.Tale;
import jakarta.persistence.Lob;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Clob;
import java.util.Objects;
import java.util.Optional;

@Getter
@Setter
public class TaleDetailResponseDto {
    private Long taleId;
    private String title;
    private String language;
//    @Lob
//    private Clob story;

    //test 반환용 코드
    private String story;

//    @Builder
//    public TaleDetailResponseDto(Tale tale,Long languageId) {
//        this.taleId = tale.getId();
//        Optional<LanguageTale> firstLanguageTale = tale.getLanguageTaleList().stream()
//                .filter(languageTale -> Objects.equals(languageTale.getLanguage().getId(), languageId))
//                .findFirst();
//        this.language = firstLanguageTale
//                .map(languageTale -> languageTale.getLanguage().getLanguageName())
//                .orElse("언어 정보 없음");
//
//        this.title = firstLanguageTale
//                .map(LanguageTale::getTitle)
//                .orElse("동화가 존재하지 않습니다");
//
//        this.story= firstLanguageTale
//                .map(LanguageTale::getStory)
//                .orElse(null);
//
//    }

//test 객체 반환용 코드
    @Builder
    public TaleDetailResponseDto(Long taleId, String title, String language, String story) {
        this.taleId = taleId;
        this.title = title;
        this.language = language;
        this.story = story;
    }
}
