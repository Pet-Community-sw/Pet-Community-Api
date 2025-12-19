package com.example.petapp.application.in.token;

import com.example.petapp.domain.token.model.Token;

import java.util.Optional;

public interface TokenQueryUseCase {
    Token findOrThrow(Long id);

    Optional<Token> find(Long id);
}
