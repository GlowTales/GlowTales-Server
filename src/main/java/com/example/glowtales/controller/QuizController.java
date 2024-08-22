package com.example.glowtales.controller;

import com.example.glowtales.domain.ResultCode;
import com.example.glowtales.dto.Result;
import com.example.glowtales.dto.request.QuizForm;
import com.example.glowtales.dto.response.quiz.TotalQuizResponseDto;
import com.example.glowtales.service.PromptService;
import com.example.glowtales.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/quiz")
@Tag(name = "Quiz API", description = "API for managing quiz")
public class QuizController {
    private final QuizService quizService;
    private final PromptService promptService;

    @Operation(summary = "#011 동화의 퀴즈와 정답 조회", description = "languageTaleId를 통해 퀴즈와 정답을 조회하는 API입니다.")
    @GetMapping("/{languageTaleId}")
    public ResponseEntity<TotalQuizResponseDto> getTotalQuizByLanguageTaleId(@PathVariable Long languageTaleId, @RequestHeader(value = "Authorization", required = true) String accessToken) {
        TotalQuizResponseDto quiz = quizService.getTotalQuizByLanguageTaleId(languageTaleId, accessToken);
        return ResponseEntity.ok(quiz);
    }


    @Operation(summary = "#011 동화의 퀴즈와 정답 조회", description = "languageTaleId를 통해 퀴즈와 정답을 조회하는 API입니다.")
    @PostMapping("/")
    public Result createQuiz(@RequestBody @Valid QuizForm quizForm, @RequestHeader(value = "Authorization") String accessToken) {
        try {
            return new Result(ResultCode.SUCCESS, quizService.createQuizzes(quizForm, accessToken));
        } catch (Exception e) {
            return new Result(ResultCode.FAIL, e.getMessage(), "400");
        }
    }

}
