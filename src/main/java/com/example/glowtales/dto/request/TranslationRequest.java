package com.example.glowtales.dto.request;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class TranslationRequest {
    private List<String> text;
    private String target_lang;

    public TranslationRequest(List<String> text, String target_lang) {
        this.text = text;
        this.target_lang = target_lang;
    }

    @Override
    public String toString() {
        return "TranslationRequest{" +
                "text=" + text +
                ", target_lang='" + target_lang + '\'' +
                '}';
    }
}
