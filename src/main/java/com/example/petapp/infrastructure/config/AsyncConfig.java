package com.example.petapp.infrastructure.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration(proxyBeanMethods = false)
@EnableAsync(order = 1) //@EnableAsync와 @EnableRetry의 order 속성을 설정하여 우선순위를 지정
@EnableRetry(order = 2)
public class AsyncConfig implements AsyncConfigurer {

    @Bean(name = "locationPipelineExecutor")
    public Executor locationPipelineExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("LocationPipelineThread-");
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
