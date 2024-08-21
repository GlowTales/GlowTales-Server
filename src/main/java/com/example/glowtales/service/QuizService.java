package com.example.glowtales.service;

import com.example.glowtales.domain.LanguageTale;
import com.example.glowtales.domain.Member;
import com.example.glowtales.domain.Quiz;
import com.example.glowtales.domain.Tale;
import com.example.glowtales.dto.response.quiz.EssayQuestionResponseDto;
import com.example.glowtales.dto.response.quiz.MultipleChoiceResponseDto;
import com.example.glowtales.dto.response.quiz.SentenceArrangementResponseDto;
import com.example.glowtales.dto.response.quiz.TotalQuizResponseDto;
import com.example.glowtales.dto.response.tale.HomeInfoResponseDto;
import com.example.glowtales.repository.LanguageTaleRepository;
import com.example.glowtales.repository.MemberRepository;
import com.example.glowtales.repository.QuizRepository;
import com.example.glowtales.repository.TaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final LanguageTaleRepository languageTaleRepository;
    private final QuizRepository quizRepository;
    private final MemberService memberService;

    public TotalQuizResponseDto getTotalQuizByLanguageTaleId(Long languageTaleId,String accessToken) {
        if (accessToken==null){
            throw new RuntimeException("accessToken이 null입니다");}
        Member member=memberService.findMemberByAccessToken(accessToken);
        if (member==null){
            throw new RuntimeException("해당 아이디에 맞는 멤버가 없습니다.");}

        if (languageTaleId == null) {
            throw new RuntimeException("languageTaleId이 null입니다");
        }
        LanguageTale languageTale = languageTaleRepository.findById(languageTaleId)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이디인 languageTale이 없습니다. 아이디: " + languageTaleId));
        if(languageTale.getTale().getMember() != member){
            throw new RuntimeException("본인의 동화에만 접근할 수 있습니다.");
        }

        List<Quiz> quizList = quizRepository.findByLanguageTale(languageTale);

        TotalQuizResponseDto totalQuizResponseDto = new TotalQuizResponseDto();

        for (Quiz quiz : quizList) {
            switch (quiz.getCd()) {
                case 1:
                    totalQuizResponseDto.addMultipleChoice(new MultipleChoiceResponseDto(quiz));
                    break;
                case 2:
                    totalQuizResponseDto.addEssayQuestion(new EssayQuestionResponseDto(quiz));
                    break;
                case 3:
                    totalQuizResponseDto.addSentenceArrangement(new SentenceArrangementResponseDto(quiz));
                    break;
                default:
                    throw new IllegalArgumentException("알 수 없는 field 값이 들어있습니다. 아이디: " + quiz.getCd());
            }
        }
        return totalQuizResponseDto;
    }
}
