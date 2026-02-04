package com.example.petapp.infrastructure.listner;

import com.example.petapp.application.in.member.object.MemberEvent;
import com.example.petapp.infrastructure.mq.RabbitConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class MemberListener {

    private final RabbitTemplate template;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(MemberEvent event) {
        template.convertAndSend(RabbitConfig.MAIN_EXCHANGE, RabbitConfig.MEMBER_ROUTING_KEY, event);
    }
}
