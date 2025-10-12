package com.example.PetApp.common.base.util;

import com.example.PetApp.common.jwt.token.JwtAuthenticationToken;
import org.springframework.security.core.Authentication;

public class AuthUtil {

    public static String getEmail(Authentication authentication) {
        return authentication.getPrincipal().toString();
    }

    public static Long getProfileId(Authentication authentication) {
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
        return jwtAuthenticationToken.getProfileId();
    }
}
