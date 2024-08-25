package com.example.glowtales.dto.response.quiz;

import com.example.glowtales.domain.Quiz;
import com.example.glowtales.domain.ResultCode;
import com.example.glowtales.dto.Result;
import com.example.glowtales.dto.response.tale.WordResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Getter
@Setter
public class QuizResponseDto {
    private Long quizId;
    private Long taleId;
    private Integer field;
    private Integer isLearned;
    private String question;
    private Long lanugageId;

    @Builder
    public QuizResponseDto(Quiz quiz) {
        this.quizId = quiz.getId();
        this.taleId = quiz.getLanguageTale().getId();
        this.field = quiz.getCd();
        this.isLearned = quiz.getIsLearned().getValue();
        this.question=quiz.getQuestion();
        this.lanugageId=quiz.getLanguageTale().getLanguage().getId();
    }
}