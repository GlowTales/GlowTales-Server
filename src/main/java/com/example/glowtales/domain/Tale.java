package com.example.glowtales.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @CreatedDate
    private LocalDateTime createdAt;

    private LocalDateTime studiedAt;

    @OneToMany(mappedBy = "tale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LanguageTale> languageTaleList = new ArrayList<>();

    //todo 단어장, 키워드 자료형 논의
    private String wordIds;
    private String keywordIds;


    @OneToMany(mappedBy = "tale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Quiz> quizList = new ArrayList<>();

    @OneToMany(mappedBy = "tale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaleWord> taleWordList = new ArrayList<>();


}
