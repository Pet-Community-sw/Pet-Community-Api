package com.example.petapp.domain.token;

import com.example.petapp.domain.token.model.Token;

import java.util.Optional;

public interface TokenRepository {
    void save(Token token);

    void delete(Long id);

    Optional<Token> find(Long id);

}
