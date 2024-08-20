package com.example.glowtales.dto.response.quiz;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TotalQuizResponseDto {
    private List<MultipleChoiceResponseDto> multipleChoices = new ArrayList<>();
    private List<EssayQuestionResponseDto> essayQuestions = new ArrayList<>();
    private List<SentenceArrangementResponseDto> sentenceArrangements = new ArrayList<>();

    public void addMultipleChoice(MultipleChoiceResponseDto dto) {
        this.multipleChoices.add(dto);
    }

    public void addEssayQuestion(EssayQuestionResponseDto dto) {
        this.essayQuestions.add(dto);
    }

    public void addSentenceArrangement(SentenceArrangementResponseDto dto) {
        this.sentenceArrangements.add(dto);
    }
}
