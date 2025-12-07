package com.example.petapp.application.in.email;

public interface EmailUseCase {

    void send(String toEmail);
    
    void verifyCode(String email, String code);
}
