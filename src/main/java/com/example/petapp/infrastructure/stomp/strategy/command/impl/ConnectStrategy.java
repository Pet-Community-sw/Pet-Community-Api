package com.example.petapp.infrastructure.stomp.strategy.command.impl;

import com.example.petapp.application.in.token.MemberInfo;
import com.example.petapp.application.out.TokenPort;
import com.example.petapp.domain.token.model.TokenType;
import com.example.petapp.infrastructure.stomp.strategy.command.StompCommandStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/*
 * CONNECTED
 * version:1.2
 * heart-beat:0,0
 * session:ws-123456
 * Spring이 알아서 CONNECTED 프레임을 보내며, 이때 sessionId가 할당됨.
 * */
@Component
@RequiredArgsConstructor
@Slf4j
public class ConnectStrategy implements StompCommandStrategy {

    private final TokenPort port;

    @Override
    public void handle(StompHeaderAccessor accessor) {
        log.info("[STOMP][CONNECT] 요청 처리 시작");

        String token = accessor.getFirstNativeHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            log.error("[STOMP][CONNECT] 유효하지 않은 토큰 헤더");
            throw new IllegalArgumentException("토큰이 없거나 형식이 잘못되었습니다.");
        }

        String accessToken = token.split(" ")[1];
        MemberInfo info = port.getInfo(TokenType.ACCESS, accessToken);

        Long profileId = info.getProfileId();

        Authentication authentication = profileId == null
                ? new UsernamePasswordAuthenticationToken(info.getMemberId(), null)
                : new UsernamePasswordAuthenticationToken(profileId, null);

        accessor.setUser(authentication);
    }
}

