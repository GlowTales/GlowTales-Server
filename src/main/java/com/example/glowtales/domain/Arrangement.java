package com.example.glowtales.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Arrangement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String word;
    private Integer orders;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;
}
