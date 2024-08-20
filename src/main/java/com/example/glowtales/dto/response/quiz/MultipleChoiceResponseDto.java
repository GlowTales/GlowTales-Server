package com.example.glowtales.dto.response.quiz;

import com.example.glowtales.domain.Choice;
import com.example.glowtales.domain.Quiz;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class MultipleChoiceResponseDto extends QuizResponseDto {
    private List<ChoiceDto> choiceList;

    @Builder
    public MultipleChoiceResponseDto(Quiz quiz) {
        super(quiz);
        this.choiceList = quiz.getChoiceList().stream()
                .map(ChoiceDto::new)
                .collect(Collectors.toList());
    }

    @Getter
    @Setter
    public static class ChoiceDto {
        private Long id;
        private String sunji;
        private Integer isCorrect;

        @Builder
        public ChoiceDto(Choice choice) {
            this.id = choice.getId();
            this.sunji = choice.getSunji();
            this.isCorrect = choice.getIsCorrect().getValue();
        }
    }
}