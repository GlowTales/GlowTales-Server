package com.example.glowtales.repository;

import com.example.glowtales.domain.Sequence;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SequenceRepository extends JpaRepository<Sequence, Long> {
}
