package com.example.glowtales.service;

import com.example.glowtales.domain.LanguageTale;
import com.example.glowtales.domain.Quiz;
import com.example.glowtales.domain.YesOrNo;
import com.example.glowtales.dto.response.tale.IsLearnedDto;
import com.example.glowtales.dto.response.tale.LanguageTaleAndIsLearnedAndLanguage;
import com.example.glowtales.dto.response.tale.LanguageTaleDto;
import com.example.glowtales.repository.LanguageTaleRepository;
import com.example.glowtales.repository.QuizRepository;
import com.example.glowtales.repository.TaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class LanguageTaleService {
    private final LanguageTaleRepository languageTaleRepository;
    private final TaleRepository taleRepository;
    private final QuizRepository quizRepository;

    @Transactional
    public void updateIsLearned(LanguageTaleDto languageTaleDto) {
        LanguageTale languageTale = languageTaleRepository.findById(languageTaleDto.getLanguageTaleId()).orElseThrow(() -> new NoSuchElementException("해당 동화가 존재하지 않습니다."));
        languageTale.updateIsLearnedAndCountAndFirstQuizCount(YesOrNo.YES, languageTale.getCount()+1, languageTaleDto.getAnswerCounts());
    }

    public IsLearnedDto getAllQuizInfo(Long taleId) {
        List<LanguageTale> languageTales = languageTaleRepository.findByTale(taleRepository.findById(taleId).orElseThrow(() -> new NoSuchElementException("해당 동화가 존재하지 않습니다.")));

        List<LanguageTaleAndIsLearnedAndLanguage> isLearnedInfos = new ArrayList<>();

        for (LanguageTale languageTale : languageTales) {
            List<Quiz> quiz = quizRepository.findByLanguageTale(languageTale);

            LanguageTaleAndIsLearnedAndLanguage isLearnedInfo = LanguageTaleAndIsLearnedAndLanguage.builder()
                    .languageTaleId(languageTale.getId())
                    .languageId(languageTale.getLanguage().getId())
                    .build();

            if (quiz.isEmpty()) {
                isLearnedInfo.setIsLearned(YesOrNo.NO);
            } else {
                isLearnedInfo.setIsLearned(YesOrNo.YES);
            }

            isLearnedInfos.add(isLearnedInfo);
        }

        IsLearnedDto isLearnedDto = new IsLearnedDto();
        isLearnedDto.setIsLearnedInfos(isLearnedInfos);

        return isLearnedDto;
    }
}
