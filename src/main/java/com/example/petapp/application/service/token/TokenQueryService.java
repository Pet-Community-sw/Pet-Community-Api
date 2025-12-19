package com.example.petapp.application.service.token;

import com.example.petapp.application.in.token.TokenQueryUseCase;
import com.example.petapp.domain.token.TokenRepository;
import com.example.petapp.domain.token.model.Token;
import com.example.petapp.interfaces.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenQueryService implements TokenQueryUseCase {

    private final TokenRepository repository;

    @Override
    public Token findOrThrow(Long id) {
        return repository.find(id).orElseThrow(() -> new NotFoundException("refreshToken이 없음. 다시 로그인."));
    }

    @Override
    public Optional<Token> find(Long id) {
        return repository.find(id);
    }
}
