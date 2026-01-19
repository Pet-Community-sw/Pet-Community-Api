package com.example.petapp.infrastructure.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync// 비동기 활성화
@EnableRetry// 재시도 활성화
public class AsyncConfig implements AsyncConfigurer {

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

    @Bean(name = "notificationExecutor")
    public Executor notificationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);// 기본 스레드 수
        executor.setMaxPoolSize(5);// 최대 스레드 수
        executor.setQueueCapacity(500);// 대기 큐 사이즈
        executor.setThreadNamePrefix("NotificationThread-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "locationInitExecutor")
    public Executor locationInitExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("LocationInitThread-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "elasticExecutor")
    public Executor elasticExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("ElasticSearchThread-");
        executor.initialize();
        return executor;
    }

    /**
     * 비동기 메서드에서 발생한 예외를 처리하기 위한 핸들러 설정
     * 메인 스레드가 모르는 비동기 작업 중 예외를 처리
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new CustomAsyncExceptionHandler();
    }
}
