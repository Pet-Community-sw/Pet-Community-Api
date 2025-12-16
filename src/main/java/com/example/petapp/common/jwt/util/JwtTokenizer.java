package com.example.petapp.common.jwt.util;

import com.example.petapp.common.exception.UnAuthorizedException;
import com.example.petapp.domain.token.model.TokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenizer {

    private static final Long ACCESS_TOKEN_EXPIRE_COUNT = 24 * 60 * 60 * 1000L;
    private static final Long REFRESH_TOKEN_EXPIRE_COUNT = 7 * 24 * 60 * 60 * 1000L;
    private static final Long EMAIL_TOKEN_EXPIRE_COUNT = 3 * 60 * 1000L;
    private final byte[] accessKey;
    private final byte[] refreshKey;


    public JwtTokenizer(@Value("${jwt.accessKey}") String accessKey, @Value("${jwt.refreshKey}") String refreshKey) {
        this.accessKey = accessKey.getBytes(StandardCharsets.UTF_8);
        this.refreshKey = refreshKey.getBytes(StandardCharsets.UTF_8);
    }

    private String createToken(Long id, Long profileId, List<String> roles, String email, Long expire, byte[] key) {
        Claims claims = Jwts.claims().setSubject(email);//todo : memberId 넣도록 수정
        claims.put("memberId", id);
        claims.put("roles", roles);
        if (profileId != null) {
            claims.put("profileId", profileId);
        }

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + expire))
                .signWith(getSigningKey(key))
                .compact();
    }

    public String create(TokenType tokenType, Long memberId, Long profileId, String email, List<String> roles) {
        switch (tokenType) {
            case ACCESS -> {
                return createToken(memberId, profileId, roles, email, ACCESS_TOKEN_EXPIRE_COUNT, accessKey);
            }
            case REFRESH -> {
                //어차피 profile선택할 때마다 refresh안줄거임왜냐면 토큰은 회원
                //유지를 도와주는거임 access재요청이있을 때 memberid에 해당하는 리프레쉬 토큰이있으면 인증 확인했다하고 access에 profile뽑아서 다시 만듦.
                //refreshToken
                return createToken(memberId, null, roles, email, REFRESH_TOKEN_EXPIRE_COUNT, refreshKey);
            }
            case EMAIL_ACCESS -> {
                return createToken(null, null, roles, email, EMAIL_TOKEN_EXPIRE_COUNT, accessKey);
            }
            default -> {
                throw new RuntimeException("지원하지 않는 tokenType");
            }
        }
    }

    private Claims parseToken(TokenType tokenType, String token) {
        byte[] key = tokenType.equals(TokenType.ACCESS) ? accessKey : refreshKey;

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey(key))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        } catch (Exception e) {
            throw new UnAuthorizedException("토큰 파싱 에러");//만료된 토큰도 파싱 에러 남.
        }
    }

    private Long getMemberId(TokenType tokenType, String token) {
        Claims claims = parseToken(tokenType, token);
        return (Long) claims.get("memberId");
    }


    private Key getSigningKey(byte[] key) {
        return Keys.hmacShaKeyFor(key);
    }

}
