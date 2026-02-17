package com.example.petapp.application.listener;

import com.example.petapp.application.common.JsonUtil;
import com.example.petapp.application.in.notification.dto.NotificationEvent;
import com.example.petapp.application.in.outbox.OutboxEventUseCase;
import com.example.petapp.domain.outboxevent.model.OutboxEvent;
import com.example.petapp.infrastructure.mq.RabbitKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class NotificationListener {

    private final JsonUtil jsonUtil;
    private final OutboxEventUseCase useCase;
    private final ApplicationEventPublisher publisher;

    @Transactional
    @EventListener
    public void handle(NotificationEvent event) {
        OutboxEvent outboxEvent = useCase.save(OutboxEvent.builder()
//                .outboxStatus(OutboxStatus.SENDING)
//                .outboxEventType(OutboxEventType.NOTIFICATION)
                        .aggregateid(String.valueOf(event.id()))
                        .aggregatetype(RabbitKeys.NOTIFICATION_ROUTING_KEY)
                        .payload(jsonUtil.toJson(event))
                        .build()
        );
        publisher.publishEvent(outboxEvent);
    }
}
