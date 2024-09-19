package com.example.glowtales.exception;

public class LanguageTaleNotFoundException extends RuntimeException {
    public LanguageTaleNotFoundException(Long taleId, Long languageId) {
        super("해당 언어로 생성된 languageTale 없습니다. tale 아이디: " + taleId + " 언어 아이디: " + languageId);
    }
}