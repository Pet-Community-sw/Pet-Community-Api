package com.example.petapp.infrastructure.config;

import com.example.petapp.infrastructure.jwt.AuthenticationManagerConfig;
import com.example.petapp.infrastructure.jwt.exception.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsUtils;

@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class SecurityConfig {
    private final AuthenticationManagerConfig authenticationManagerConfig;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(form -> form.disable())
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .httpBasic(httpBasic -> httpBasic.disable())
                .authorizeHttpRequests(auth -> auth
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .requestMatchers("/error", "/error/**").permitAll()
                .requestMatchers(
                        "/swagger",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/v3/api-docs.yaml",
                        "/swagger-resources/**",
                        "/webjars/**"
                ).permitAll()
                .requestMatchers("/image/profiles/**", "/image/members/**", "/image/posts/**", "/image/basic/**", "/favicon.ico").permitAll()
                .requestMatchers("/ws-stomp/**", "/pub/**", "/sub/**").permitAll()
                .requestMatchers("/members", "/members/login").permitAll()
                .requestMatchers("/auth", "/auth/emails", "/auth/emails/verify").permitAll()
                .requestMatchers("/token").permitAll()
                // 임시 비밀번호 발급 후 비밀번호 변경은 TEMPORARY 권한도 허용
                .requestMatchers("/members/reset-password").hasAnyRole("USER", "TEMPORARY")
                //ROLE_안붙여도 spring security가 자동으로 붙여줌
                //여기서 설정 후 @PreAuthorize 설정 불가능 config에서 막히는게 우선순위가 더 높음
                .requestMatchers("/**").hasAnyRole("USER")
                .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception.authenticationEntryPoint(customAuthenticationEntryPoint))
                .with(authenticationManagerConfig, Customizer.withDefaults())
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
