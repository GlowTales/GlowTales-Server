package com.example.glowtales.exception;

public class LanguageIdNullException extends RuntimeException {
    public LanguageIdNullException() {
        super("languageId가 null입니다");
    }
}