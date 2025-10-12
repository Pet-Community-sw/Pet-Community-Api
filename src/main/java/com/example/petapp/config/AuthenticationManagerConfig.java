package com.example.petapp.config;

import com.example.petapp.common.jwt.filter.JwtAuthenticationFilter;
import com.example.petapp.common.jwt.provider.JwtAuthenticationProvider;
import com.example.petapp.infrastructure.redis.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class AuthenticationManagerConfig extends AbstractHttpConfigurer<AuthenticationManagerConfig, HttpSecurity> {

    private final JwtAuthenticationProvider authenticationProvider;
    private final RedisUtil redisUtil1;

    @Override
    public void configure(HttpSecurity builder) throws Exception {
        AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);

        builder.addFilterBefore(
                new JwtAuthenticationFilter(authenticationManager, redisUtil1),
                UsernamePasswordAuthenticationFilter.class
        ).authenticationProvider(authenticationProvider);
    }
}
