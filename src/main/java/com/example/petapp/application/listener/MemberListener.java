package com.example.petapp.application.listener;

import com.example.petapp.application.common.JsonUtil;
import com.example.petapp.application.in.member.object.MemberEvent;
import com.example.petapp.application.in.outbox.OutboxEventUseCase;
import com.example.petapp.domain.outboxevent.model.OutboxEvent;
import com.example.petapp.infrastructure.mq.RabbitKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberListener {

    private final OutboxEventUseCase useCase;
    private final JsonUtil jsonUtil;

    @EventListener
    public void handle(MemberEvent event) {
        useCase.save(OutboxEvent.builder()
                .routingKey(RabbitKeys.MEMBER_ROUTING_KEY)
                .payload(jsonUtil.toJson(event))
                .build()
        );
    }
}
