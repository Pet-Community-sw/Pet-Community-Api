package com.example.petapp.infrastructure.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;

@Slf4j
public class CustomAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {
    @Override
    public void handleUncaughtException(Throwable throwable, Method method, Object... obj) {
        log.error("비동기 메서드 실행 중 예외 발생 - 클래스: {}, 메시지: {}", method.getDeclaringClass().getSimpleName(), throwable.getMessage());
    }
}
