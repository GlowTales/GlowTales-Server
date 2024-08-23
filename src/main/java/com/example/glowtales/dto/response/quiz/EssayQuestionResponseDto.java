package com.example.glowtales.dto.response.quiz;

import com.example.glowtales.domain.Quiz;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EssayQuestionResponseDto extends QuizResponseDto {
    private String answer;

    @Builder(builderMethodName = "essayQuestionResponseDtoBuilder")
    public EssayQuestionResponseDto(Quiz quiz) {
        super(quiz);
        this.answer = quiz.getAnswer();
    }

}
