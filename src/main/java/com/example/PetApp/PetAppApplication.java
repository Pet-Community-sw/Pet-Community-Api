package com.example.PetApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

//@SpringBootApplication
@SpringBootApplication(scanBasePackages = "com.example.PetApp")
@EnableJpaRepositories(basePackages = "com.example.PetApp")
@EntityScan(basePackages = "com.example.PetApp")//이걸 해야지만 전체가 컴포넌트 스캔을 함 이거 이유를 좀 찾고싶음.
public class PetAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(PetAppApplication.class, args);
	}

}
