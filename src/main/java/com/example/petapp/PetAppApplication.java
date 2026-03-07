package com.example.petapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PetAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(PetAppApplication.class, args);//내부에서 컨테이너 자동 생성
    }
}
