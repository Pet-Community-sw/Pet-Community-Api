package com.example.petapp.application.listener;

import com.example.petapp.application.common.JsonUtil;
import com.example.petapp.application.in.email.EmailEvent;
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
public class EmailListener {

    public final ApplicationEventPublisher publisher;
    private final OutboxEventUseCase useCase;
    private final JsonUtil jsonUtil;

    @EventListener
    public void handle(EmailEvent event) {
        OutboxEvent outboxEvent = useCase.save(OutboxEvent.builder()
                .outboxStatus(OutboxStatus.SENDING)
                .outboxEventType(OutboxEventType.EMAIL)
                .aggregateId(event.id())
                .payload(jsonUtil.toJson(event))
                .build()
        );

        publisher.publishEvent(outboxEvent);
    }
}
