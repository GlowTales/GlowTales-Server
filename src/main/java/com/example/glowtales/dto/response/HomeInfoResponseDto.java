package com.example.glowtales.dto.response;

import com.example.glowtales.domain.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class HomeInfoResponseDto {


    private int created_tale_count;
    private Long studied_tale_count;
    private int study_count;
    private List<String> learning_language_list;

    @Builder
    public HomeInfoResponseDto(Member member) {
        this.created_tale_count = (member.getTale_list() != null) ? member.getTale_list().size() : 0;
        this.studied_tale_count = member.getTale_list().stream()
                .flatMap(tale -> tale.getLanguage_tale_list().stream())
                .filter(languageTale -> languageTale.getIs_learned().getValue() == 1)
                .count(); //
        this.study_count= member.getTale_list().stream()
                .flatMap(tale -> tale.getLanguage_tale_list().stream())
                .mapToInt(LanguageTale::getCount)
                .sum();

        this.learning_language_list=member.getLearning_language_list().stream()
                .map(LearningLanguage::getLanguage)
                .map(Language::getLanguage_name)
                .collect(Collectors.toList());

    }
}