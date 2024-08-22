package com.example.glowtales.service;

import com.example.glowtales.domain.Language;
import com.example.glowtales.dto.language.LanguageDto;
import com.example.glowtales.repository.LanguageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LanguageService {
    private final LanguageRepository languageRepository;

    public List<LanguageDto> getLanguage() {
        List<LanguageDto> languageDtos = new ArrayList<>();
        for (Language language : languageRepository.findAll()) {
            languageDtos.add(LanguageDto.of(language));
        }
        return languageDtos;
    }
}
