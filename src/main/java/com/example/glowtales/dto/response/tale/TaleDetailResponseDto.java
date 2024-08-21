package com.example.glowtales.dto.response.tale;

import com.example.glowtales.domain.Language;
import com.example.glowtales.domain.LanguageTale;
import com.example.glowtales.domain.Tale;
import com.example.glowtales.domain.YesOrNo;
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
    private Long languageTaleId;
    private String title;
    private String languageName;
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
    public TaleDetailResponseDto(Long taleId, Long languageTaleId, String title, String languageName, String story) {
        this.taleId = taleId;
        this.languageTaleId = languageTaleId;
        this.title = title;
        this.languageName = languageName;
        this.story = story;
    }

    public TaleDetailResponseDto(String title, String languageName, String story) {
        this.title = title;
        this.languageName = languageName;
        this.story = story;
    }

    public static TaleDetailResponseDto of(LanguageTale languageTale, Tale tale) {
        return TaleDetailResponseDto.builder()
                .languageTaleId(languageTale.getId())
                .taleId(tale.getId())
                .title(languageTale.getTitle())
                .languageName(languageTale.getLanguage().getLanguageName())
                .story(languageTale.getStory())
                .build();
    }

    public static LanguageTale to(TaleDetailResponseDto taleDetailResponseDto, Tale tale, Language language) {
        return LanguageTale.builder()
                .language(language)
                .tale(tale)
                .isLearned(YesOrNo.NO)
                .count(0)
                .title(taleDetailResponseDto.getTitle())
                .story(taleDetailResponseDto.getStory())
                .build();
    }
}
