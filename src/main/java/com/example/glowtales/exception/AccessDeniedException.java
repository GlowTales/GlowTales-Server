package com.example.glowtales.exception;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException() {
        super("본인의 동화에만 접근할 수 있습니다.");
    }
}