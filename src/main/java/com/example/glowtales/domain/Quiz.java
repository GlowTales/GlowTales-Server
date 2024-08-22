package com.example.glowtales.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer cd;

    private String question;

    @Enumerated(EnumType.STRING)
    private YesOrNo isLearned;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tale_id")
    private LanguageTale languageTale;

//    @OneToOne(mappedBy = "quiz")
//    private Answer answer;

    private String answer;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Choice> choiceList = new ArrayList<>();

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Arrangement> arrangementList = new ArrayList<>();

    @Builder
    public Quiz(Integer cd, String question, YesOrNo isLearned, LanguageTale languageTale, String answer) {
        this.cd = cd;
        this.question = question;
        this.isLearned = isLearned;
        this.languageTale = languageTale;
        this.answer = answer;
    }
}
