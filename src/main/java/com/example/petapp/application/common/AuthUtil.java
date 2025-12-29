package com.example.petapp.application.common;

import com.example.petapp.infrastructure.jwt.token.JwtAuthenticationToken;
import org.springframework.security.core.Authentication;

public class AuthUtil {

    public static Long getMemberId(Authentication authentication) {
        return Long.valueOf(authentication.getPrincipal().toString());
    }

    public static Long getProfileId(Authentication authentication) {
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
        return jwtAuthenticationToken.getProfileId();
    }
}
