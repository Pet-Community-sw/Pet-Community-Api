package com.example.PetApp.domain.email;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface EmailService {
    void sendMail(String email);

    void verifyCode(String email, String code);
}
