package com.example.glowtales.repository;

import com.example.glowtales.domain.Member;
import com.example.glowtales.domain.Tale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaleRepository extends JpaRepository<Tale, Long> {
    List<Tale> findByMemberOrderByCreatedAtDesc(Member member);
    List<Tale> findByMember(Member member);
}
