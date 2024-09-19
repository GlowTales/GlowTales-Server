package com.example.glowtales;

import com.example.glowtales.domain.ResultCode;
import com.example.glowtales.dto.Result;
import com.example.glowtales.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(TaleNotFoundException.class)
    public ResponseEntity<Result<Object>> handleTaleNotFoundException(TaleNotFoundException e) {
        Result<Object> result = new Result<>(
                ResultCode.FAIL,
                e.getMessage(),
                "404"
        );
        return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(LanguageTaleNotFoundException.class)
    public ResponseEntity<Result<Object>> handleLanguageTaleNotFoundException(LanguageTaleNotFoundException e) {
        Result<Object> result = new Result<>(
                ResultCode.FAIL,
                e.getMessage(),
                "404"
        );
        return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Result<Object>> handleAccessDeniedException(AccessDeniedException e) {
        Result<Object> result = new Result<>(
                ResultCode.FAIL,
                e.getMessage(),
                "403"
        );

        return new ResponseEntity<>(result, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AccessTokenNullException.class)
    public ResponseEntity<Result<Object>> handleAccessTokenNullException(AccessTokenNullException e) {
        Result<Object> result = new Result<>(
                ResultCode.FAIL,
                e.getMessage(),
                "400"
        );
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<Result<Object>> handleMemberNotFoundException(MemberNotFoundException e) {
        Result<Object> result = new Result<>(
                ResultCode.FAIL,
                e.getMessage(),
                "404"
        );
        return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TaleIdNullException.class)
    public ResponseEntity<Result<Object>> handleTaleIdNullException(TaleIdNullException e) {
        Result<Object> result = new Result<>(
                ResultCode.FAIL,
                e.getMessage(),
                "400"
        );
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(LanguageIdNullException.class)
    public ResponseEntity<Result<Object>> handleLanguageIdNullException(LanguageIdNullException e) {
        Result<Object> result = new Result<>(
                ResultCode.FAIL,
                e.getMessage(),
                "400"
        );
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(LanguageNotFoundException.class)
    public ResponseEntity<Result<Object>> handleLanguageNotFoundException(LanguageNotFoundException e) {
        logger.error("Unhandled Exception 발생: {}", e.getMessage());
        Result<Object> result = new Result<>(
                ResultCode.FAIL,
                e.getMessage(),
                "404"
        );
        System.out.println("111111111");
        logger.error("에러코드: {}", result.getCode());
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
