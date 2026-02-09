package com.example.petapp.infrastructure.database.adapter;

import com.example.petapp.domain.token.TokenRepository;
import com.example.petapp.domain.token.model.Token;
import com.example.petapp.infrastructure.database.jpa.token.JpaTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TokenRepositoryAdapter implements TokenRepository {

    private final JpaTokenRepository repository;

    @Override
    public void save(Token token) {
        repository.save(token);
    }

    @Override
    public void delete(Long id) {
        repository.deleteByMemberId(id);
    }

    @Override
    public Optional<Token> find(Long id) {
        return repository.findByMemberId(id);
    }
}
