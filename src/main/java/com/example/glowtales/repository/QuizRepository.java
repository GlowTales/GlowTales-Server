package com.example.glowtales.repository;

import com.example.glowtales.domain.LanguageTale;
import com.example.glowtales.domain.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByLanguageTale(LanguageTale languageTale);
}
