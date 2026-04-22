package com.example.petapp.application.listener;

import com.example.petapp.application.common.JsonUtil;
import com.example.petapp.application.in.notification.dto.NotificationEvent;
import com.example.petapp.application.in.outbox.OutboxEventUseCase;
import com.example.petapp.domain.outboxevent.model.OutboxEvent;
import com.example.petapp.infrastructure.mq.RabbitKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class NotificationListener {

    private final JsonUtil jsonUtil;
    private final OutboxEventUseCase useCase;

    @Transactional
    @EventListener
    public void handle(NotificationEvent event) {
        useCase.save(OutboxEvent.builder()
                .routingKey(RabbitKeys.NOTIFICATION_ROUTING_KEY)
                .payload(jsonUtil.toJson(event))
                .build()
        );
    }
}
