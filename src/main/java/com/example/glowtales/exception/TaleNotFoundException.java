package com.example.glowtales.exception;

public class TaleNotFoundException extends RuntimeException {
    public TaleNotFoundException(Long taleId) {
        super("해당 아이디인 tale이 없습니다. 아이디: " + taleId);
    }
}