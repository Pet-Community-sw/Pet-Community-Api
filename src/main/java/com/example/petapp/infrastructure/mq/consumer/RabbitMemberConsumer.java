package com.example.petapp.infrastructure.mq.consumer;

import com.example.petapp.application.in.member.MemberSearchUseCase;
import com.example.petapp.infrastructure.mq.RabbitKeys;
import com.example.petapp.infrastructure.mq.RabbitRetryHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitMemberConsumer {

    private final MemberSearchUseCase useCase;
    private final RabbitRetryHandler rabbitRetryHandler;

    @RabbitListener(queues = RabbitKeys.MEMBER_QUEUE)
    public void hande(OutboxMessage outboxMessage, Message message) {
        try {
            useCase.handle(outboxMessage);
        } catch (Exception e) {
            rabbitRetryHandler.handle(outboxMessage, message, e);
        }
    }
}
