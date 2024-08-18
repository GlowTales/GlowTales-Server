package com.example.glowtales.dto.response;

import com.example.glowtales.domain.Tale;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TaleResponseDto {
    private Long tale_id;
    private LocalDateTime created_at;
    private Long member_id;
    private LanguageTaleTitleResponseDto title;

    @Builder
    public TaleResponseDto(Tale tale) {
        this.tale_id = tale.getId();
        this.created_at = tale.getCreated_at();
        this.member_id = tale.getMember().getId();
        this.title = tale.getLanguage_tale_list().stream()
//                .filter(languageTale -> isKorean(languageTale.getLanguage().getId()))
                .findFirst()
                .map(LanguageTaleTitleResponseDto::new)
                .orElse(null);

    }

//    private boolean isKorean(Long language_id) {
//        return language_id == 1;
//    }
}