package com.example.petapp.application.listener;

import com.example.petapp.application.common.JsonUtil;
import com.example.petapp.application.in.email.EmailEvent;
import com.example.petapp.application.in.outbox.OutboxEventUseCase;
import com.example.petapp.domain.outboxevent.model.OutboxEvent;
import com.example.petapp.infrastructure.mq.RabbitKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class EmailListener {

    //    private final ApplicationEventPublisher publisher;
    private final OutboxEventUseCase useCase;
    private final JsonUtil jsonUtil;

    @Transactional
    @EventListener
    public void handle(EmailEvent event) {
        OutboxEvent outboxEvent = useCase.save(OutboxEvent.builder()
//                .outboxStatus(OutboxStatus.SENDING) //스케줄링 시 중복 발송 방지
//                .outboxEventType(OutboxEventType.EMAIL)
                        .routingKey(RabbitKeys.MAIL_ROUTING_KEY)
                        .payload(jsonUtil.toJson(event))
                        .build()
        );

//        publisher.publishEvent(outboxEvent);
    }
}
