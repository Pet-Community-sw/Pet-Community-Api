package com.example.petapp.infrastructure.database.jpa.token;

import com.example.petapp.domain.token.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaTokenRepository extends JpaRepository<Token, Long> {
    
    void deleteByMemberId(Long memberId);

    Optional<Token> findByMemberId(Long memberId);
}
