package com.example.glowtales.dto.response.quiz;

import com.example.glowtales.domain.Answer;
import com.example.glowtales.domain.Quiz;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EssayQuestionResponseDto extends QuizResponseDto {
    private AnswerDto answer;

    @Builder(builderMethodName = "essayQuestionResponseDtoBuilder")
    public EssayQuestionResponseDto(Quiz quiz) {
        super(quiz);
        this.answer = new AnswerDto(quiz.getAnswer());
    }

    @Getter
    @Setter
    public static class AnswerDto {
        private Long id;
        private String answer;

        @Builder
        public AnswerDto(Answer answer) {
            this.id = answer.getId();
            this.answer = answer.getAnswer();
        }
    }
}
