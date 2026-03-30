package com.example.petapp.infrastructure.mail;

import com.example.petapp.application.common.JsonUtil;
import com.example.petapp.application.in.email.EmailEvent;
import com.example.petapp.application.out.cache.EmailCachePort;
import com.example.petapp.infrastructure.mq.consumer.OutboxMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Random;

@Slf4j
@Component
@ConditionalOnProperty(name = "mail.provider", havingValue = "smtp")
@RequiredArgsConstructor
public class SmtpProvider implements MailProvider {

    private final JavaMailSender javaMailSender;
    private final EmailCachePort port;
    private final JsonUtil jsonUtil;

    @Value("${spring.mail.username}")
    private String email;

    @Override
    public void send(OutboxMessage outboxMessage) {
        EmailEvent event = jsonUtil.fromJson(outboxMessage.getPayload(), EmailEvent.class);
        if (!port.exist(event.getToEmail())) { //멱등 처리

            MimeMessage message = javaMailSender.createMimeMessage();
            String code = buildCode();
            try {
                message.setRecipients(Message.RecipientType.TO, event.getToEmail());
                message.setSubject(event.getSubject());
                message.setText(buildBody(code), "utf-8", "html");
                message.setFrom(new InternetAddress(email, "멍냥로드"));
                //InternetAddress: 이메일 주소를 RFC 표준 형식으로 감싸는 객체
                javaMailSender.send(message);
                port.createWithDuration(event.getToEmail(), code, 3 * 60L);
                log.info("메일 전송");
            } catch (Exception e) {
                port.delete(event.getToEmail()); //메일 전송 실패 시 캐시에서 제거하여 재시도 시 새로운 코드로 메일이 전송되도록 함
                throw new RuntimeException(e);
            }
        }
    }

    private String buildBody(String code) {
        return "<div>" +
                "인증코드를 확인해주세요.<br><strong style=\"font-size: 30px;\">" +
                code +
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
