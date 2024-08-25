package com.example.glowtales.dto.response.member;

import com.example.glowtales.domain.YesOrNo;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LearningLanguageResponseDto {
    private YesOrNo isLearned;
    private String learningLevel;
}
