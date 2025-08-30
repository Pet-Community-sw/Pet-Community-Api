package com.example.PetApp.domain.token;

import com.example.PetApp.domain.member.model.entity.Member;
import com.example.PetApp.domain.token.model.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshRepository extends JpaRepository<RefreshToken, Long> {
    void deleteByMemberId(Long memberId);


    Optional<RefreshToken> findByMember(Member memberId);


    Optional<RefreshToken> findByMemberId(Long memberId);

}
