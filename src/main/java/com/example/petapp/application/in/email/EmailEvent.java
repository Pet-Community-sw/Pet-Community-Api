package com.example.petapp.application.in.email;

// 이벤트 객체(불변)
public record EmailEvent(
        Long id,
        String toEmail,
        String subject,
        String code
) {
}
