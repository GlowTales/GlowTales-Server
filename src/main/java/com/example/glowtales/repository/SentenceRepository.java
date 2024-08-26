package com.example.glowtales.repository;

import com.example.glowtales.domain.LanguageTale;
import com.example.glowtales.domain.Sentence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SentenceRepository extends JpaRepository<Sentence, Long> {
    List<Sentence> findByLanguageTale(LanguageTale languageTale);
}
