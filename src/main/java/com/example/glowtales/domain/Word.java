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
public class Word {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_id")
    private Language language;

    private String mark;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origin_word_id")
    private Word originWord;

    @OneToMany(mappedBy = "originWord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Word> newWordList = new ArrayList<>();

    @OneToMany(mappedBy = "word", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaleWord> taleWordList = new ArrayList<>();

    @Builder
    public Word(Language language, String mark, Word originWord) {
        this.language = language;
        this.mark = mark;
        this.originWord = originWord;
    }
}
