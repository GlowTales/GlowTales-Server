package com.example.glowtales.domain;

import com.example.glowtales.converter.YesOrNoConverter;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Choice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    //TODO 변수명 변경 필요
    private String sunji;

    @Convert(converter = YesOrNoConverter.class)
    private YesOrNo isCorrect;

    @Builder
    public Choice(Quiz quiz, String sunji, YesOrNo isCorrect) {
        this.quiz = quiz;
        this.sunji = sunji;
        this.isCorrect = isCorrect;
    }
}
