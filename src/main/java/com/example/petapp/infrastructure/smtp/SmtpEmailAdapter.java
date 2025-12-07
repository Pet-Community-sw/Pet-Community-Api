package com.example.petapp.infrastructure.smtp;

import com.example.petapp.application.out.EmailPort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Component
@RequiredArgsConstructor
public class SmtpEmailAdapter implements EmailPort {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String email;

    @Override
    public void send(String toEmail, String subject, String body) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            message.setRecipients(Message.RecipientType.TO, toEmail);
            message.setSubject(subject);
            message.setText(body, "utf-8", "html");
            message.setFrom(new InternetAddress(email, "멍냥로드"));
            //InternetAddress: 이메일 주소를 RFC 표준 형식으로 감싸는 객체
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        javaMailSender.send(message);
    }
}
