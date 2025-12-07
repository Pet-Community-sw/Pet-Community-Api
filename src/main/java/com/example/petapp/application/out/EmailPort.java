package com.example.petapp.application.out;

public interface EmailPort {
    void send(String toEmail, String subject, String body);
}
