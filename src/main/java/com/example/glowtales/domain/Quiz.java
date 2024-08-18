package com.example.glowtales.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
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

    @Enumerated(EnumType.STRING)
    private YesOrNo is_learned;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tale_id")
    private Tale tale;

    @OneToOne(mappedBy = "quiz")
    private Answer answer;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Choice> choice_list = new ArrayList<>();

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Sequence> squence_list = new ArrayList<>();
}
