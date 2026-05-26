package com.example.petapp.application.usecase.email;

public interface EmailUseCase {

    void send(Long id, String toEmail);

    void verifyCode(String email, String code);
}
