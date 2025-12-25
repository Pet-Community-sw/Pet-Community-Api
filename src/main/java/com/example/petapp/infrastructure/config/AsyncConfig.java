package com.example.petapp.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync// 비동기 활성화
public class AsyncConfig {

    @Bean(name = "mailExecutor")
    public Executor mailExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);// 기본 스레드 수
        executor.setMaxPoolSize(5);// 최대 스레드 수
        executor.setQueueCapacity(500);// 대기 큐 사이즈
        executor.setThreadNamePrefix("MailThread-");
        executor.initialize();
        return executor;
    }
}
