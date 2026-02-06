package com.example.petapp.infrastructure.mq.consumer;

import com.example.petapp.domain.outboxevent.model.OutboxEvent;
import com.example.petapp.infrastructure.mail.MailProvider;
import com.example.petapp.infrastructure.mq.RabbitKeys;
import com.example.petapp.infrastructure.mq.RabbitRetryHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitEmailConsumer {

    private final RabbitRetryHandler rabbitRetryHandler;
    private final MailProvider mailProvider;

    @RabbitListener(queues = RabbitKeys.MAIL_QUEUE)
    public void handle(OutboxEvent event, Message message) {//메시지 본문과 메타데이터를 받음
        try {
            mailProvider.send(event);
        } catch (Exception e) {
            rabbitRetryHandler.handle(event, message, e);
        }
    }

}
