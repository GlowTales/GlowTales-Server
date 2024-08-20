package com.example.glowtales.domain;

import com.example.glowtales.converter.YesOrNoConverter;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Clob;
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

    @Lob
    private Clob story;

    private String title;

    @Convert(converter = YesOrNoConverter.class)
    private YesOrNo isLearned;

    private Integer count;
    @OneToMany(mappedBy = "languageTale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Quiz> quizList = new ArrayList<>();

}