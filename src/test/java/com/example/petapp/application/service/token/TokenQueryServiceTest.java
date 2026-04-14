package com.example.petapp.application.service.token;

import com.example.petapp.domain.token.TokenRepository;
import com.example.petapp.domain.token.model.Token;
import com.example.petapp.interfaces.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenQueryServiceTest {

    @Mock
    private TokenRepository repository;

    @InjectMocks
    private TokenQueryService tokenQueryService;

    @Test
    void 토큰이_존재하면_findOrThrow_조회에_성공한다() {
        Token token = Token.builder().refreshToken("refresh-token").build();
        when(repository.find(1L)).thenReturn(Optional.of(token));

        Token result = tokenQueryService.findOrThrow(1L);

        assertThat(result).isSameAs(token);
        verify(repository).find(1L);
    }

    @Test
    void 토큰이_없으면_findOrThrow에서_예외가_발생한다() {
        when(repository.find(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tokenQueryService.findOrThrow(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("refreshToken이 없음. 다시 로그인.");
    }

    @Test
    void find는_저장소_결과를_그대로_반환한다() {
        Token token = Token.builder().refreshToken("refresh-token").build();
        when(repository.find(1L)).thenReturn(Optional.of(token));

        Optional<Token> result = tokenQueryService.find(1L);

        assertThat(result).contains(token);
        verify(repository).find(1L);
    }
}
