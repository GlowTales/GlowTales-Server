package com.example.glowtales.exception;

public class TaleIdNullException extends RuntimeException {
    public TaleIdNullException() {
        super("TaleId가 null입니다");
    }
}