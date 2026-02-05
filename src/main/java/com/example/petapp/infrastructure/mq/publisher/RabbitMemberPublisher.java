package com.example.petapp.infrastructure.mq.publisher;

import com.example.petapp.application.common.JsonUtil;
import com.example.petapp.application.in.member.object.MemberEvent;
import com.example.petapp.application.in.outbox.OutboxEventUseCase;
import com.example.petapp.domain.outboxevent.model.OutboxEvent;
import com.example.petapp.domain.outboxevent.model.OutboxEventType;
import com.example.petapp.domain.outboxevent.model.OutboxStatus;
import com.example.petapp.infrastructure.mq.RabbitKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class RabbitMemberPublisher {

    private final RabbitTemplate template;
    private final OutboxEventUseCase useCase;
    private final JsonUtil jsonUtil;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(MemberEvent event) {
        useCase.save(OutboxEvent.builder()
                .outboxStatus(OutboxStatus.SENDING)
                .outboxEventType(OutboxEventType.MEMBER)
                .aggregateId(event.getMemberId())
                .payload(jsonUtil.toJson(event))
                .build()
        );
        template.convertAndSend(RabbitKeys.MAIN_EXCHANGE, RabbitKeys.MEMBER_ROUTING_KEY, event);
    }
}
