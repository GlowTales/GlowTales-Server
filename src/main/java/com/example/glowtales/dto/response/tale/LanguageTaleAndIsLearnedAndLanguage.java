package com.example.glowtales.dto.response.tale;

import com.example.glowtales.domain.YesOrNo;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LanguageTaleAndIsLearnedAndLanguage {
    private Long languageTaleId;
    private YesOrNo isLearned;
    private Long languageId;
}
