package com.example.glowtales.repository;

import com.example.glowtales.domain.LanguageTale;
import com.example.glowtales.domain.LanguageTaleWord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LanguageTaleWordRepository extends JpaRepository<LanguageTaleWord, Long> {
    List<LanguageTaleWord> findByLanguageTale(LanguageTale languageTale);
}
