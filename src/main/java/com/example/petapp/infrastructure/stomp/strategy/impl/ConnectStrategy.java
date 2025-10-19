package com.example.petapp.infrastructure.stomp.strategy.impl;

import com.example.petapp.common.jwt.util.JwtTokenizer;
import com.example.petapp.domain.member.MemberRepository;
import com.example.petapp.domain.member.model.entity.Member;
import com.example.petapp.domain.token.TokenType;
import com.example.petapp.infrastructure.stomp.strategy.StompCommandStrategy;
import io.jsonwebtoken.Claims;
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

    private final JwtTokenizer jwtTokenizer;
    private final MemberRepository memberRepository;

    @Override
    public void handle(StompHeaderAccessor accessor) {
        log.info("[STOMP][CONNECT] 요청 처리 시작");

        String token = accessor.getFirstNativeHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            log.error("[STOMP][CONNECT] 유효하지 않은 토큰 헤더");
            throw new IllegalArgumentException("토큰이 없거나 형식이 잘못되었습니다.");
        }

        String accessToken = token.split(" ")[1];
        if (jwtTokenizer.isTokenExpired(TokenType.ACCESS, accessToken)) {
            log.error("[STOMP][CONNECT] 만료된 토큰");
            throw new IllegalArgumentException("만료된 토큰입니다.");
        }

        Claims claims = jwtTokenizer.parseAccessToken(accessToken);
        Object profileId = claims.get("profileId");

        Authentication authentication;
        if (profileId == null) {
            String email = claims.getSubject();
            Member member = memberRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("해당 이메일의 사용자를 찾을 수 없습니다."));
            authentication = new UsernamePasswordAuthenticationToken(member.getId(), null);
            log.info("[STOMP][CONNECT] memberId 인증 완료: {}", member.getId());
        } else {
            authentication = new UsernamePasswordAuthenticationToken(profileId, null);
            log.info("[STOMP][CONNECT] profileId 인증 완료: {}", profileId);
        }

        accessor.setUser(authentication);
    }
}

