package com.example.glowtales.service;

import com.example.glowtales.domain.*;
import com.example.glowtales.dto.request.QuizForm;
import com.example.glowtales.dto.response.quiz.*;
import com.example.glowtales.repository.*;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final LanguageTaleRepository languageTaleRepository;
    private final QuizRepository quizRepository;
    private final MemberService memberService;
    private final LearningLanguageRepository learningLanguageRepository;
    private final LanguageRepository languageRepository;
    private final PromptService promptService;
    private final WordRepository wordRepository;
    private final LanguageTaleWordRepository languageTaleWordRepository;
    private final TaleRepository taleRepository;
    private final SentenceRepository sentenceRepository;
    private final ChoiceRepository choiceRepository;
    private final SequenceRepository sequenceRepository;

    public TotalQuizResponseDto getTotalQuizByLanguageTaleId(Long languageTaleId, String accessToken) {
        if (accessToken == null) {
            throw new RuntimeException("accessToken이 null입니다");
        }
        Member member = memberService.findMemberByAccessToken(accessToken);
        if (member == null) {
            throw new RuntimeException("해당 아이디에 맞는 멤버가 없습니다.");
        }

        if (languageTaleId == null) {
            throw new RuntimeException("languageTaleId이 null입니다");
        }
        LanguageTale languageTale = languageTaleRepository.findById(languageTaleId)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이디인 languageTale이 없습니다. 아이디: " + languageTaleId));
        if (languageTale.getTale().getMember() != member) {
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
//                    totalQuizResponseDto.addEssayQuestion(new EssayQuestionResponseDto(quiz));
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

    @Transactional
    public KeywordsAndKeySentencesDto createQuizzes(QuizForm quizForm, String accessToken) throws Exception {
        // 이미 선택한 학습언어에 대한 학습 수준에 대한 정보가 있을 경우
        if (quizForm.getLearningLevel() == null) {
            Member member = memberService.findMemberByAccessToken(accessToken);
            LearningLanguage learningLanguage = learningLanguageRepository.findByMemberAndLanguage(
                    member,
                    languageRepository.findById(quizForm.getLanguageId())
                            .orElseThrow(() -> new NoSuchElementException("해당 정보가 존재하지 않습니다.")));
            quizForm.setLearningLevel(learningLanguage.getLearningLevel());
        }

        // 핵심단어 & 핵심문장 생성
//        promptService.createQuiz();
        JSONObject jsonObject = promptService.testQuiz();
        System.out.println("jsonObject: " + jsonObject);

        Language language = languageRepository.findById(quizForm.getLanguageId()).orElseThrow(() -> new NoSuchElementException("해당 언어가 존재하지 않습니다."));
        Tale tale = taleRepository.findById(quizForm.getTaleId()).orElseThrow(() -> new NoSuchElementException("해당 동화가 존재하지 않습니다."));
        LanguageTale languageTale = languageTaleRepository.findByLanguageAndTale(language, tale);

        List<KeyResponseDto> keywordDtos = new ArrayList<>();

        // 핵심단어 저장
        JSONArray keywords = jsonObject.getJSONArray("keywords");
        for (int i = 0; i < keywords.length(); i++) {
            JSONArray keywordPair = keywords.getJSONArray(i);
            String mark = keywordPair.getString(0);
            String meaning = keywordPair.getString(1);
            Word originWord = wordRepository.findByMark(meaning);

            // 한국어가 먼저 있는지 확인 -> 없으면 한국어 word 먼저 저장
            if (originWord == null) {
                originWord = wordRepository.save(Word.builder()
                        .language(languageRepository.findByLanguageName("Korean"))
                        .mark(meaning)
                        .build());
            }

            // 키워드가 이미 있는지도 확인 -> 키워드가 미리 저장된 것이 없을 경우 저장
            Word byMarkAndOriginWord = wordRepository.findByMarkAndOriginWord(mark, originWord);

            if (byMarkAndOriginWord == null) {

                byMarkAndOriginWord = wordRepository.save(Word.builder()
                        .originWord(originWord)
                        .language(language)
                        .mark(mark)
                        .build());
            }

            // TaleWord 객체 생성
            languageTaleWordRepository.save(LanguageTaleWord.builder()
                    .languageTale(languageTale)
                    .word(byMarkAndOriginWord)
                    .build());

            keywordDtos.add(new KeyResponseDto(byMarkAndOriginWord.getMark(), originWord.getMark()));
        }

        List<KeyResponseDto> keysentenceDtos = new ArrayList<>();

        // 핵심문장 저장
        JSONArray keySentences = jsonObject.getJSONArray("keySentences");
        for (int i = 0; i < keySentences.length(); i++) {
            JSONArray sentencePair = keySentences.getJSONArray(i);
            String sentence = sentencePair.getString(0);
            String translation = sentencePair.getString(1);

            sentenceRepository.save(Sentence.builder()
                    .languageTale(languageTale)
                    .sentence(sentence)
                    .translation(translation)
                    .build());

            keysentenceDtos.add(new KeyResponseDto(sentence, translation));
        }

        // 퀴즈 생성
        JSONArray quizzes = jsonObject.getJSONArray("quizzes");

        for (int i = 0; i < quizzes.length(); i++) {
            JSONObject quizObject = quizzes.getJSONObject(i);
            // 1. 객관식 퀴즈 & 정답 생성
            if (quizObject.has("multiple")) {
                JSONObject multipleChoice = quizObject.getJSONObject("multiple");
                String word = multipleChoice.getString("word");
                JSONArray choices = multipleChoice.getJSONArray("choices");
                String answer = multipleChoice.getString("answer");

                // 퀴즈 생성
                Quiz quiz = quizRepository.save(
                        Quiz.builder()
                                .cd(1)
                                .question(word + "의 뜻은 무엇인가요?")
                                .isLearned(YesOrNo.NO)
                                .languageTale(languageTale)
                                .build()
                );

                // 정답 저장
                choiceRepository.save(
                        Choice.builder()
                                .quiz(quiz)
                                .sunji(answer)
                                .isCorrect(YesOrNo.YES)
                                .build()
                );

                for (int j = 0; j < choices.length(); j++) {
                    // 선지 저장
                    choiceRepository.save(
                            Choice.builder()
                                    .quiz(quiz)
                                    .sunji(choices.getString(i))
                                    .isCorrect(YesOrNo.NO)
                                    .build()
                    );
                }
                System.out.println("객관식 저장 완료");
                // 2. 주관식 퀴즈 & 정답 생성
            } else if (quizObject.has("short")) {
                JSONObject shortAnswer = quizObject.getJSONObject("short");
                String word = shortAnswer.getString("word");
                String answer = shortAnswer.getString("answer");

                // 퀴즈 & 정답 저장
                Quiz quiz = quizRepository.save(
                        Quiz.builder()
                                .cd(2)
                                .isLearned(YesOrNo.NO)
                                .question(word + "의 뜻은 무엇인가요?")
                                .languageTale(languageTale)
                                .build()
                );

                System.out.println("주관식 저장 완료");
                // 3. 문자 배열 퀴즈 & 정답 생성
            } else if (quizObject.has("order")) {
                JSONObject orderQuiz = quizObject.getJSONObject("order");
                String meaning = orderQuiz.getString("quiz");
                JSONArray sequences = orderQuiz.getJSONArray("sequences");

                // 퀴즈 저장
                Quiz quiz = quizRepository.save(Quiz.builder()
                        .question(meaning)
                        .isLearned(YesOrNo.NO)
                        .cd(3)
                        .languageTale(languageTale)
                        .build());

                // 시퀀스 저장
                for (int j = 0; j < sequences.length(); j++) {
                    sequenceRepository.save(
                            Sequence.builder()
                                    .orders(j+1)
                                    .quiz(quiz)
                                    .word(sequences.getString(j))
                            .build());
                }

                System.out.println("문자 배열 저장 완료");
            }
        }
        return new KeywordsAndKeySentencesDto(keywordDtos, keysentenceDtos);
    }
}
