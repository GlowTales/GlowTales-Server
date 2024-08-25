package com.example.glowtales.dto.response.tale;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class IsLearnedDto {
    List<LanguageTaleAndIsLearnedAndLanguage> isLearnedInfos;
}
