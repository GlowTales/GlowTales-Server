package com.example.glowtales.dto.response.tale;

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

