package com.example.glowtales.domain;

import com.example.glowtales.converter.YesOrNoConverter;
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
public class LanguageTale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_id")
    private Language language;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tale_id")
    private Tale tale;

    @Column(columnDefinition = "TEXT")
    private String story;

    private String title;

    private Integer fistQuizCount;

    @Convert(converter = YesOrNoConverter.class)
    private YesOrNo isLearned;

    private Integer count;
    @OneToMany(mappedBy = "languageTale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Quiz> quizList = new ArrayList<>();

    @OneToMany(mappedBy = "languageTale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LanguageTaleWord> languageTaleWordList = new ArrayList<>();


    @Builder
    public LanguageTale(Language language, Tale tale, String story, String title, YesOrNo isLearned, Integer count) {
        this.language = language;
        this.tale = tale;
        this.story = story;
        this.title = title;
        this.isLearned = isLearned;
        this.count = count;
    }
}