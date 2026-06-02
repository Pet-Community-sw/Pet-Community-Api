package com.example.petapp.infrastructure.stomp.strategy.command.impl;

import com.example.petapp.application.common.exception.ErrorCode;
import com.example.petapp.application.common.exception.PetCommunityException;
import com.example.petapp.application.out.TokenPort;
import com.example.petapp.application.usecase.member.MemberUseCase;
import com.example.petapp.application.usecase.token.MemberInfo;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.token.model.TokenType;
import com.example.petapp.infrastructure.stomp.strategy.command.StompCommandStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

/*
 * CONNECTED
 * version:1.2
 * heart-beat:0,0
 * session:ws-123456
 * Spring이 CONNECTED 프레임을 보내며, 이때 sessionId가 할당됨.
 * */
@Component
@RequiredArgsConstructor
@Slf4j
public class ConnectStrategy implements StompCommandStrategy {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private final TokenPort port;
    private final MemberUseCase memberUseCase;

    @Override
    public void handle(StompHeaderAccessor accessor) {
        log.info("[STOMP][CONNECT] 요청 처리 시작");

        String token = accessor.getFirstNativeHeader(AUTHORIZATION_HEADER);
        if (token == null || !token.startsWith(BEARER_PREFIX)) {
            log.error("[STOMP][CONNECT] 유효하지 않은 토큰 헤더");
            throw new PetCommunityException(ErrorCode.UNAUTHORIZED, "토큰이 없거나 형식이 잘못되었습니다.");
        }

        String accessToken = token.substring(BEARER_PREFIX.length());
        MemberInfo info = port.getInfo(TokenType.ACCESS, accessToken);
        Member member = memberUseCase.findOrThrow(info.getMemberId());

        accessor.setUser(() -> member.getId().toString());
    }

    @Override
    public StompCommand getCommand() {
        return StompCommand.CONNECT;
    }
}
