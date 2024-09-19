package com.example.glowtales.exception;

public class AccessTokenNullException extends RuntimeException {
    public AccessTokenNullException() {
        super("accessToken이 null입니다");
    }
}