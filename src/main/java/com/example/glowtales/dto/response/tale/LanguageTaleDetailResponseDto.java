package com.example.glowtales.dto.response.tale;

import com.example.glowtales.domain.LanguageTale;
import com.example.glowtales.domain.Tale;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

@Getter
@Setter
public class LanguageTaleDetailResponseDto {
    private Long taleId;
    private Long languageTaleId;
    private String title;
    private String language;
    private String story;  // Clob을 String으로 변경

    @Builder
    public LanguageTaleDetailResponseDto(Tale tale, Long languageId) {
        this.taleId = tale.getId();
        Optional<LanguageTale> firstLanguageTale = tale.getLanguageTaleList().stream()
                .filter(languageTale -> Objects.equals(languageTale.getLanguage().getId(), languageId))
                .findFirst();
        this.languageTaleId = firstLanguageTale.map(LanguageTale::getId).orElse(null);
        this.language = firstLanguageTale
                .map(languageTale -> languageTale.getLanguage().getLanguageName())
                .orElse("언어 정보 없음");

        this.title = firstLanguageTale
                .map(LanguageTale::getTitle)
                .orElse("동화가 존재하지 않습니다");

        this.story = firstLanguageTale
                .map(languageTale -> {
                    try {
                        return convertClobToString(languageTale.getStory());
                    } catch (SQLException e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .orElse(null);
    }

    private String convertClobToString(Clob clob) throws SQLException {
        if (clob == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        try (Reader reader = clob.getCharacterStream()) {
            char[] buffer = new char[1024];
            int bytesRead;
            while ((bytesRead = reader.read(buffer)) != -1) {
                sb.append(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
