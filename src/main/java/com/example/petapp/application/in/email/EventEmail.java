package com.example.petapp.application.in.email;

// 이벤트 객체
public record EventEmail(
        String toEmail,
        String subject,
        String code
) {
}
