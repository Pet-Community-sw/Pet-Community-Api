package com.example.petapp.infrastructure.jwt.filter;

import com.example.petapp.application.out.cache.TokenCachePort;
import com.example.petapp.infrastructure.jwt.exception.JwtExceptionCode;
import com.example.petapp.infrastructure.jwt.token.JwtAuthenticationToken;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /**
     * OncePerRequestFilter는 http요청당 한 번 씩 실행되는 filter
     */
    private final AuthenticationManager authenticationManager;
    private final TokenCachePort port;

    @Override//filter 하지않게 하려고
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String uri = request.getRequestURI();
        return uri.startsWith("/token")
                || uri.startsWith("/swagger-ui")
                || uri.startsWith("/v3/api-docs")
                || uri.startsWith("/swagger-resources")
                || uri.startsWith("/webjars")
                || uri.startsWith("/error");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = getToken(request);
        if (StringUtils.hasText(token)) {
            try {
                authenticationToken(token);
            } catch (ExpiredJwtException e) {
                request.setAttribute("exception", JwtExceptionCode.EXPIRED_TOKEN.getCode());
                log.warn("[JWT] Expired token. token: {}", token);
                throw new BadCredentialsException("expired token", e);
            } catch (UnsupportedJwtException e) {
                request.setAttribute("exception", JwtExceptionCode.UNSUPPORTED_TOKEN.getCode());
                log.warn("[JWT] Unsupported token. token: {}", token);
                throw new BadCredentialsException("unsupported token", e);
            } catch (MalformedJwtException | SecurityException e) {
                request.setAttribute("exception", JwtExceptionCode.INVALID_TOKEN.getCode());
                log.warn("[JWT] Invalid token. token: {}", token);
                throw new BadCredentialsException("invalid token", e);
            } catch (IllegalStateException | NullPointerException e) {
                request.setAttribute("exception", JwtExceptionCode.NOT_FOUND_TOKEN.getCode());
                log.warn("[JWT] Token not found or illegal state.");
                throw new BadCredentialsException("not found token", e);
            } catch (JwtException e) { // 기타 JWT 예외
                request.setAttribute("exception", JwtExceptionCode.INVALID_TOKEN.getCode());
                log.warn("[JWT] JwtException occurred. token: {}, msg: {}", token, e.getMessage());
                throw new BadCredentialsException("jwt exception", e);
            }
        }
        filterChain.doFilter(request, response); //다음 filterChain을 호출
    }


    private void authenticationToken(String token) {
        JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(token);
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext()
                .setAuthentication(authenticate);
    }

    public String getToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer")) {
            String[] arr = authorization.split(" ");
            if (port.exist(arr[1])) {
                throw new BadCredentialsException("로그아웃된 토큰입니다.");
            }
            return arr[1];
        }
        return null;
    }
}