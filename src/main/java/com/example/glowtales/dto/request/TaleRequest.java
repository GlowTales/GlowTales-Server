package com.example.glowtales.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TaleRequest {
    private List<String> keywords;
    private String detail;
    private String mood;
    private String characters;
    private String contents;
}
