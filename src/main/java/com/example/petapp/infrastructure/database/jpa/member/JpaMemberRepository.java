package com.example.petapp.infrastructure.database.jpa.member;

import com.example.petapp.domain.member.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JpaMemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String email);

    Optional<Member> findByEmail(String email);

    Optional<Member> findByPhoneNumber(String phoneNumber);

    @Query("select m.name from Member m where m.id in : ids")
    List<String> findNamesByIds(@Param("ids") List<Long> ids);
}

