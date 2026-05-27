package com.example.petapp.application.usecase.email.service;

import com.example.petapp.application.out.cache.EmailCachePort;
import com.example.petapp.application.usecase.email.EmailEvent;
import com.example.petapp.application.usecase.email.EmailUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService implements EmailUseCase {

    private final EmailCachePort emailCachePort;
    private final ApplicationEventPublisher publisher;

    @Override
    public void send(Long id, String toEmail) {
        if (emailCachePort.exists(toEmail)) {
            emailCachePort.deleteAuthCode(toEmail);
        }
        publisher.publishEvent(new EmailEvent(id, toEmail, "멍냥로드 인증코드 안내입니다."));
    }

    @Override
    public void verifyCode(String email, String code) {
        String authCode = emailCachePort.findAuthCode(email);
        log.info("email : {}, code : {}", email, code);
        if (authCode == null) {
            throw new IllegalArgumentException("인증번호가 만료되었습니다. 다시 시도해주세요.");
        } else if (!(authCode.equals(code))) {
            throw new IllegalArgumentException("인증번호가 일지하치 않습니다.");
        }
    }
}
