package com.example.petapp.application.in.token;

import com.example.petapp.domain.token.model.Token;

public interface TokenQueryUseCase {
    Token findOrThrow(Long id);
}
