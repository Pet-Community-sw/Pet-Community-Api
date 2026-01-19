package com.example.petapp.infrastructure.database.jpa;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.example.petapp.infrastructure.database.jpa"//JPA 리포지토리 스캔 위치 지정
)
@EnableJpaAuditing//감사 기능을 활성화 @CreatedDate, @LastModifiedDate 등을 사용할 수 있게 함
public class JpaConfig {
}
