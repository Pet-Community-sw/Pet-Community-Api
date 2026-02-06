package com.example.petapp.application.listener;

import com.example.petapp.application.common.JsonUtil;
import com.example.petapp.application.in.notification.dto.NotificationEvent;
import com.example.petapp.application.in.outbox.OutboxEventUseCase;
import com.example.petapp.domain.outboxevent.model.OutboxEvent;
import com.example.petapp.domain.outboxevent.model.OutboxEventType;
import com.example.petapp.domain.outboxevent.model.OutboxStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationListener {

    private final OutboxEventUseCase useCase;
    private final JsonUtil jsonUtil;
    private final ApplicationEventPublisher publisher;

    @EventListener
    public void handle(NotificationEvent event) {
        OutboxEvent outboxEvent = useCase.save(OutboxEvent.builder()
                .outboxStatus(OutboxStatus.SENDING)
                .outboxEventType(OutboxEventType.NOTIFICATION)
                .aggregateId(event.id())
                .payload(jsonUtil.toJson(event))
                .build()
        );
        publisher.publishEvent(outboxEvent);
    }
}
