package com.example.glowtales.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sentence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sentence;
    private String translation;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "language_tale_id")
    private LanguageTale languageTale;

    @Builder
    public Sentence(String sentence, String translation, LanguageTale languageTale) {
        this.sentence = sentence;
        this.translation = translation;
        this.languageTale = languageTale;
    }
}
