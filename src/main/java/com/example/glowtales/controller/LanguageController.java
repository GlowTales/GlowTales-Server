package com.example.glowtales.controller;

import com.example.glowtales.domain.ResultCode;
import com.example.glowtales.dto.Result;
import com.example.glowtales.service.LanguageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/languages")
@Tag(name = "Language API", description = "API for managing quiz")
public class LanguageController {
    private final LanguageService languageService;

    @GetMapping("/")
    public Result getLanguages() {
        try {
            return new Result(ResultCode.SUCCESS, languageService.getLanguage());
        } catch (Exception e) {
            return new Result(ResultCode.FAIL, e.getMessage(), "400");
        }

    }
}
