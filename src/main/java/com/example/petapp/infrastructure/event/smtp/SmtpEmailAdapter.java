package com.example.petapp.infrastructure.event.smtp;

import com.example.petapp.application.in.email.EventEmail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Slf4j
@Component
@RequiredArgsConstructor
public class SmtpEmailAdapter {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String email;

    @Async("mailExecutor")
    @EventListener
    @Retryable(
            maxAttempts = 4,// 최대 재시도 횟수(기본값 3)
            backoff = @Backoff(delay = 2000, multiplier = 2.0, random = true)// 재시도 간격
    )
    public void handle(EventEmail event) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            message.setRecipients(Message.RecipientType.TO, event.toEmail());
            message.setSubject(event.subject());
            message.setText(buildBody(event.code()), "utf-8", "html");
            message.setFrom(new InternetAddress(email, "멍냥로드"));
            //InternetAddress: 이메일 주소를 RFC 표준 형식으로 감싸는 객체
            javaMailSender.send(message);
            log.info("메일 전송");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String buildBody(String code) {
        return "<div>" +
                "인증코드를 확인해주세요.<br><strong style=\"font-size: 30px;\">" +
                code +
                "</strong><br>인증코드는 3분간 유지됩니다.</div>";
    }
}
