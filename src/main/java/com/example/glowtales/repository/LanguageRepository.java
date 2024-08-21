package com.example.glowtales.repository;

import com.example.glowtales.domain.Language;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageRepository extends JpaRepository<Language, Long> {
    Language findByLanguageName(String languageName);
}
