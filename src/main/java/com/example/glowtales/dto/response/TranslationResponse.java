package com.example.glowtales.dto.response;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class TranslationResponse {
    private List<Translation> translations;

    @Getter
    @Setter
    public static class Translation {
        private String text;
    }
}

