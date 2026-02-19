package com.example.petapp.infrastructure.config;

import com.example.petapp.infrastructure.jwt.AuthenticationManagerConfig;
import com.example.petapp.infrastructure.jwt.exception.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsUtils;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final AuthenticationManagerConfig authenticationManagerConfig;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin().disable()
                .csrf().disable()
                .cors()
                .and()
                .httpBasic().disable()
                .authorizeRequests()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .mvcMatchers("/error", "/error/**").permitAll()
                .antMatchers(
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/v3/api-docs.yaml",
                        "/swagger-resources/**",
                        "/actuator/prometheus",
                        "/webjars/**"
                ).permitAll()
                .antMatchers("/image/profiles/**", "/image/members/**", "/image/posts/**", "/image/basic/**", "/favicon.ico").permitAll()
                .mvcMatchers("/ws-stomp/**", "/pub/**", "/sub/**").permitAll()
                .mvcMatchers("/members/signup", "/members/login", "/members/find-id", "/members/send-email", "/members/verify-code").permitAll()
                .mvcMatchers("/token").permitAll()
                // 임시 비밀번호 발급 후 비밀번호 변경은 TEMPORARY 권한도 허용
                .mvcMatchers("/members/reset-password").hasAnyRole("USER", "TEMPORARY")
                //ROLE_안붙여도 spring security가 자동으로 붙여줌
                //여기서 설정 후 @PreAuthorize 설정 불가능 config에서 막히는게 우선순위가 더 높음
                .mvcMatchers("/**").hasAnyRole("USER")
                .anyRequest().authenticated()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(customAuthenticationEntryPoint)
                .and()
                .apply(authenticationManagerConfig)
                .and()
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
