package com.example.glowtales.repository;

import com.example.glowtales.domain.LanguageTale;
import com.example.glowtales.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageTaleRepository extends JpaRepository<LanguageTale, Long> {
}
