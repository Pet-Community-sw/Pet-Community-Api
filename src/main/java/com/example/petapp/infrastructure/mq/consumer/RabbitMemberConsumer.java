package com.example.petapp.infrastructure.mq.consumer;

import com.example.petapp.application.in.member.MemberSearchUseCase;
import com.example.petapp.application.in.member.object.MemberEvent;
import com.example.petapp.infrastructure.mq.RabbitConfig;
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

    @RabbitListener(queues = RabbitConfig.MEMBER_QUEUE)
    public void hande(MemberEvent event, Message message) {
        try {
            useCase.handle(event);
        } catch (Exception e) {
            rabbitRetryHandler.handle(event, message, e);
        }
    }
}
