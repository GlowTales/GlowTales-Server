package com.example.glowtales.repository;

import com.example.glowtales.domain.Sentence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SentenceRepository extends JpaRepository<Sentence, Long> {
}
