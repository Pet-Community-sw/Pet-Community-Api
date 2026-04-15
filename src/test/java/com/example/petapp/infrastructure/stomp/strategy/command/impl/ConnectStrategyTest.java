package com.example.petapp.infrastructure.stomp.strategy.command.impl;

import com.example.petapp.application.in.member.MemberQueryUseCase;
import com.example.petapp.application.in.token.MemberInfo;
import com.example.petapp.application.out.TokenPort;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.token.model.TokenType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConnectStrategyTest {

    @Mock
    private TokenPort port;
    @Mock
    private MemberQueryUseCase memberQueryUseCase;

    @InjectMocks
    private ConnectStrategy connectStrategy;

    @Test
    void Authorization헤더가_없으면_예외가_발생한다() {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);

        assertThatThrownBy(() -> connectStrategy.handle(accessor))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("토큰이 없거나 형식이 잘못되었습니다.");
    }

    @Test
    void 정상토큰이면_userPrincipal을_설정한다() {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        accessor.setNativeHeader("Authorization", "Bearer access-token");
        Member member = org.mockito.Mockito.mock(Member.class);
        when(port.getInfo(TokenType.ACCESS, "access-token")).thenReturn(
                MemberInfo.builder().memberId(7L).build()
        );
        when(memberQueryUseCase.findOrThrow(7L)).thenReturn(member);
        when(member.getId()).thenReturn(7L);

        connectStrategy.handle(accessor);

        assertThat(accessor.getUser()).isNotNull();
        assertThat(accessor.getUser().getName()).isEqualTo("7");
    }
}
