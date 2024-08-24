package com.example.glowtales.dto.response.quiz;

import com.example.glowtales.domain.Quiz;
import com.example.glowtales.domain.Sequence;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class SentenceArrangementResponseDto extends QuizResponseDto {
    private List<SequenceDto> sequenceList;

    public SentenceArrangementResponseDto(Quiz quiz) {
        super(quiz);
        this.sequenceList = quiz.getSequenceList().stream()
                .map(SequenceDto::new)
                .collect(Collectors.toList());
    }

    @Getter
    @Setter
    public static class SequenceDto {
        private String word;
        private Integer order;

        @Builder
        public SequenceDto(Sequence arrangement) {
            this.word = arrangement.getWord();
            this.order = arrangement.getOrders();
        }
    }
}