package com.example.petapp.infrastructure.jwt;

import com.example.petapp.application.out.cache.TokenCachePort;
import com.example.petapp.infrastructure.jwt.filter.JwtAuthenticationFilter;
import com.example.petapp.infrastructure.jwt.provider.JwtAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class AuthenticationManagerConfig extends AbstractHttpConfigurer<AuthenticationManagerConfig, HttpSecurity> {

    private final JwtAuthenticationProvider authenticationProvider;
    private final TokenCachePort port;

    @Override
    public void configure(HttpSecurity builder) throws Exception {
        AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);

        builder.addFilterBefore(
                new JwtAuthenticationFilter(authenticationManager, port),
                //todo : 수정해야할듯 인터페이스로
                UsernamePasswordAuthenticationFilter.class
        ).authenticationProvider(authenticationProvider);
    }
}
