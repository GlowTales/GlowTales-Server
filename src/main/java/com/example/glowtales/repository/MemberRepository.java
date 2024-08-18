package com.example.glowtales.repository;

import com.example.glowtales.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByName(String name);
    Member findByEmail(String email);
    Member findByLoginId(String loginId);
}

