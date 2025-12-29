package com.example.petapp.infrastructure.jwt.provider;

import com.example.petapp.application.in.token.MemberInfo;
import com.example.petapp.domain.token.model.TokenType;
import com.example.petapp.infrastructure.jwt.token.JwtAuthenticationToken;
import com.example.petapp.infrastructure.jwt.util.JwtTokenizer;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtTokenizer jwtTokenizer;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) authentication;
        MemberInfo info = jwtTokenizer.getInfo(TokenType.ACCESS, authenticationToken.getToken());
        String email = info.getName();
        Object profileId = info.getProfileId();
        List<GrantedAuthority> authorities = getGrantedAuthority(info.getRoles());
        return new JwtAuthenticationToken(authorities, email, null, profileId);
    }

    private List<GrantedAuthority> getGrantedAuthority(List<String> roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : roles) {
            authorities.add(() -> role);
        }
        return authorities;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
