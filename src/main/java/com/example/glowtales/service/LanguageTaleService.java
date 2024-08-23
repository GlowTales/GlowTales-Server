package com.example.glowtales.service;

import com.example.glowtales.domain.LanguageTale;
import com.example.glowtales.domain.YesOrNo;
import com.example.glowtales.dto.response.tale.LanguageTaleDto;
import com.example.glowtales.repository.LanguageTaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class LanguageTaleService {
    private final LanguageTaleRepository languageTaleRepository;

    @Transactional
    public void updateIsLearned(LanguageTaleDto languageTaleDto) {
        LanguageTale languageTale = languageTaleRepository.findById(languageTaleDto.getLanguageTaleId()).orElseThrow(() -> new NoSuchElementException("해당 동화가 존재하지 않습니다."));
        languageTale.updateIsLearnedAndCountAndFirstQuizCount(YesOrNo.YES, languageTale.getCount()+1, languageTaleDto.getAnswerCounts());
    }
}
