//package com.example.petapp.infrastructure.smtp;
//
//import com.example.petapp.application.in.email.EventEmail;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.context.event.EventListener;
//import org.springframework.retry.annotation.Backoff;
//import org.springframework.retry.annotation.Retryable;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Component;
//import software.amazon.awssdk.services.ses.SesClient;
//import software.amazon.awssdk.services.ses.model.SendEmailRequest;
//
/// **
// * AWS SES를
// * 이용한 이메일
// * 전송 어댑터
// * 배포 시
// * 사용 예정.
// */
//
//@Slf4j
//@Component
//@ConditionalOnProperty(name = "mail.provider", havingValue = "ses")
//@RequiredArgsConstructor
//public class AwsSesAdapter {
//
//    private final SesClient sesClient;
//
//    @Value("${spring.mail.username}")
//    private String email;
//
//    @Async("mailExecutor")
//    @EventListener
//    @Retryable(
//            value = {Exception.class},
//            maxAttempts = 3,
//            backoff = @Backoff(delay = 2000, multiplier = 2)
//    )
//    public void handle(EventEmail event) {
//        try {
//            SendEmailRequest request = SendEmailRequest.builder()
//                    .destination(d -> d.toAddresses(event.toEmail()))
//                    .message(m -> m
//                            .subject(s -> s.data(event.subject()))
//                            .body(b -> b.html(h -> h.data(buildBody(event.code()))))
//                    )
//                    .source(email)
//                    .build();
//
//            sesClient.sendEmail(request);
//            log.info("메일 전송");
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private String buildBody(String code) {
//        return "<div>" +
//                "인증코드를 확인해주세요.<br><strong style=\"font-size: 30px;\">" +
//                code +
//                "</strong><br>인증코드는 3분간 유지됩니다.</div>";
//    }
//}
