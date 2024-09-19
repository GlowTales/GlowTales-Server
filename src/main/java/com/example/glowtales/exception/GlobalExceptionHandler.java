package com.example.glowtales.exception;

import com.example.glowtales.domain.ResultCode;
import com.example.glowtales.dto.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LanguageNotFoundException.class)
    public ResponseEntity<Result<Object>> handleLanguageNotFoundException(LanguageNotFoundException e) {
        Result<Object> result = new Result<>(
                ResultCode.FAIL,
                e.getMessage(),
                "404"
        );
        return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Object>> handleException(Exception e) {
        Result<Object> result = new Result<>(
                ResultCode.FAIL,
                "예상치 못한 오류가 발생했습니다: " + e.getMessage(),
                "500"
        );
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
