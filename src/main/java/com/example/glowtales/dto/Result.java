package com.example.glowtales.dto;

import com.example.glowtales.domain.ResultCode;
import lombok.Getter;

import java.util.List;

@Getter
public class Result<T> {

    private ResultCode result;
    private T data;
    private String message;
    private String code;
    private List<Error> errors;

    public Result(ResultCode result, T data, String message, String code) {
        this.result = result;
        this.data = data;
        this.message = message;
        this.code = code;
    }

    public Result(ResultCode result, String code, List<Error> errors) {
        this.result = result;
        this.code = code;
        this.errors = errors;
    }

    public Result(ResultCode result, T data) {
        this.result = result;
        this.data = data;
    }

    public Result(ResultCode result, String message, String code) {
        this.result = result;
        this.message = message;
        this.code = code;
    }
}
