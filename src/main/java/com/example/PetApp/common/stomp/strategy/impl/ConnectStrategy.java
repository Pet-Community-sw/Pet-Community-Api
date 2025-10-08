package com.example.PetApp.common.stomp.strategy.impl;

import com.example.PetApp.common.jwt.util.JwtTokenizer;
import com.example.PetApp.common.stomp.strategy.StompCommandStrategy;
import com.example.PetApp.domain.member.MemberRepository;
import com.example.PetApp.domain.member.model.entity.Member;
import com.example.PetApp.domain.token.TokenType;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConnectStrategy implements StompCommandStrategy {

    private final JwtTokenizer jwtTokenizer;
    private final MemberRepository memberRepository;

    @Override
    public void handle(StompHeaderAccessor accessor) {
        log.info("[STOMP] command 전략 시작");

        String token = accessor.getFirstNativeHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new IllegalArgumentException("토큰이 없거나 형식이 잘못되었습니다.");
        }

        String accessToken = token.split(" ")[1];
        if (jwtTokenizer.validateToken(TokenType.ACCESS, accessToken)) {
            throw new IllegalArgumentException("유효하지 않는 토큰입니다.");
        }

        Claims claims = jwtTokenizer.parseAccessToken(accessToken);
        Object profileId = claims.get("profileId");//토큰에서 꺼내는거임.

        Authentication authentication;
        if (profileId == null) {
            String email = claims.getSubject();
            Member member = memberRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("해당 이메일의 사용자를 찾을 수 없습니다."));
            authentication = new UsernamePasswordAuthenticationToken(member.getId(), null);
            log.info("[STOMP] member 인증 : {}", member.getId());
        } else {
            authentication = new UsernamePasswordAuthenticationToken(profileId, null);
            log.info("[STOMP] profile 인증 : {}", profileId);
        }

        accessor.setUser(authentication);
    }
}

