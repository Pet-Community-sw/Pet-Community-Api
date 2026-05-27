package com.example.petapp.infrastructure.jwt.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String exception = (String) request.getAttribute("exception");
        log.error("Commence Get Exception : {}", exception);

        if (JwtExceptionCode.NOT_FOUND_TOKEN.getCode().equals(exception)) {
            log.error("entry point >> not found null");
            setResponse(response, JwtExceptionCode.NOT_FOUND_TOKEN);
        } else if (JwtExceptionCode.INVALID_TOKEN.getCode().equals(exception)) {
            log.error("entry point >> invalid token");
            setResponse(response, JwtExceptionCode.INVALID_TOKEN);
        } else if (JwtExceptionCode.EXPIRED_TOKEN.getCode().equals(exception)) {
            log.error("entry point >> expired token");
            setResponse(response, JwtExceptionCode.EXPIRED_TOKEN);
        } else if (JwtExceptionCode.UNSUPPORTED_TOKEN.getCode().equals(exception)) {
            log.error("entry point >> unsupported token");
            setResponse(response, JwtExceptionCode.UNSUPPORTED_TOKEN);
        } else {
            setResponse(response, JwtExceptionCode.UNKNOWN_ERROR);
        }
    }

    private void setResponse(HttpServletResponse response, JwtExceptionCode exceptionCode) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        Map<String, Object> errorInfo = new HashMap<>();
        errorInfo.put("message", exceptionCode.getMessage());
        errorInfo.put("code", exceptionCode.getCode());
        String responseJson = objectMapper.writeValueAsString(errorInfo);
        response.getWriter().print(responseJson);
    }
}
