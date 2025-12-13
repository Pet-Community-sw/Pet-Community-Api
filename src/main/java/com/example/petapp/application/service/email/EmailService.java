package com.example.petapp.application.service.email;

import com.example.petapp.application.in.email.EmailUseCase;
import com.example.petapp.application.out.EmailPort;
import com.example.petapp.application.out.cache.EmailCachePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService implements EmailUseCase {

    private final EmailCachePort emailCachePort;
    private final EmailPort emailPort;

    @Override
    public void send(String toEmail) {
        if (emailCachePort.exist(toEmail)) {
            emailCachePort.delete(toEmail);
        }
        String emailCode = buildCode();

        emailCachePort.createWithDuration(toEmail, emailCode, 3 * 60L);
        emailPort.send(toEmail, "멍냥로드 인증코드 안내입니다.", buildBody(emailCode));
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

    private String buildBody(String emailCode) {
        return "<div>" +
                "인증코드를 확인해주세요.<br><strong style=\"font-size: 30px;\">" +
                emailCode +
                "</strong><br>인증코드는 3분간 유지됩니다.</div>";
    }

    private String buildCode() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 6; i++) {
            int n = random.nextInt(10);
            sb.append(n);
        }
        return sb.toString();
    }
}
