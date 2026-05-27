package com.example.petapp.application.usecase.token.service;

import com.example.petapp.application.common.exception.ErrorCode;
import com.example.petapp.application.common.exception.PetCommunityException;
import com.example.petapp.application.usecase.token.TokenQueryUseCase;
import com.example.petapp.domain.token.TokenRepository;
import com.example.petapp.domain.token.model.Token;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenQueryService implements TokenQueryUseCase {

    private final TokenRepository repository;

    @Override
    public Token findOrThrow(Long id) {
        return repository.find(id).orElseThrow(() -> new PetCommunityException(ErrorCode.NOT_FOUND, "refreshToken이 없음. 다시 로그인."));
    }

    @Override
    public Optional<Token> find(Long id) {
        return repository.find(id);
    }
}
