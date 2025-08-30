package com.example.PetApp.repository.jpa;

import com.example.PetApp.domain.Member;
import com.example.PetApp.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshRepository extends JpaRepository<RefreshToken, Long> {
    void deleteByMemberId(Long memberId);


    Optional<RefreshToken> findByMember(Member memberId);


    Optional<RefreshToken> findByMemberId(Long memberId);

}
