package com.example.glowtales.repository;

import com.example.glowtales.domain.LearningLanguage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LearningLanguageRepository extends JpaRepository<LearningLanguage, Long> {
    List<LearningLanguage> findByMemberId(Long memberId);
}
