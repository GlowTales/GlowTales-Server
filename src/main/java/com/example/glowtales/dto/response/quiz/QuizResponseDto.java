package com.example.glowtales.dto.response.quiz;

import com.example.glowtales.domain.Quiz;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizResponseDto {
    private Long quizId;
    private Long taleId;
    private Integer field;
    private Integer isLearned;
    private String question;

    @Builder
    public QuizResponseDto(Quiz quiz) {
        this.quizId = quiz.getId();
        this.taleId = quiz.getLanguageTale().getId();
        this.field = quiz.getCd();
        this.isLearned = quiz.getIsLearned().getValue();
        this.question=quiz.getQuestion();
    }
}
