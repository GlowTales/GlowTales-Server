package com.example.glowtales.repository;

import com.example.glowtales.domain.Language;
import com.example.glowtales.domain.LanguageTale;
import com.example.glowtales.domain.Tale;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageTaleRepository extends JpaRepository<LanguageTale, Long> {
    LanguageTale findByLanguageAndTale(Language language, Tale tale);
}
