package com.example.glowtales.repository;

import com.example.glowtales.domain.Word;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WordRepository extends JpaRepository<Word, Long> {
    Word findByMarkAndOriginWord(String mark, Word originWord);
    Word findByMark(String mark);
}
