package com.example.glowtales.exception;

public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException() {
        super("해당 아이디에 맞는 멤버가 없습니다.");
    }
}