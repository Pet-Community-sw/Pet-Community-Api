package com.example.petapp.application.service.email;

import com.example.petapp.application.in.email.EmailEvent;
import com.example.petapp.application.in.email.EmailUseCase;
import com.example.petapp.application.out.cache.EmailCachePort;
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
        if (emailCachePort.exist(toEmail)) {
            emailCachePort.delete(toEmail);
        }
        publisher.publishEvent(new EmailEvent(id, toEmail, "멍냥로드 인증코드 안내입니다."));
    }

    @Override
    public void verifyCode(String email, String code) {
        String authCode = emailCachePort.get(email);
        log.info("email : {}, code : {}", email, code);
        if (authCode == null) {
            throw new IllegalArgumentException("인증번호가 만료되었습니다. 다시 시도해주세요.");
        } else if (!(authCode.equals(code))) {
            throw new IllegalArgumentException("인증번호가 일지하치 않습니다.");
        }
    }
}
