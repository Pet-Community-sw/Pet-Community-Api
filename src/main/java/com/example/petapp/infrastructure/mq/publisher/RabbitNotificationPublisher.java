package com.example.petapp.infrastructure.mq.publisher;

import com.example.petapp.application.common.JsonUtil;
import com.example.petapp.application.in.notification.dto.NotificationEvent;
import com.example.petapp.application.in.outbox.OutboxEventUseCase;
import com.example.petapp.domain.outboxevent.model.OutboxEvent;
import com.example.petapp.domain.outboxevent.model.OutboxEventType;
import com.example.petapp.domain.outboxevent.model.OutboxStatus;
import com.example.petapp.infrastructure.mq.RabbitKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitNotificationPublisher {

    public final RabbitTemplate template;
    public final OutboxEventUseCase useCase;
    public final JsonUtil jsonUtil;

    @EventListener
    public void handle(NotificationEvent event) {
        OutboxEvent saved = useCase.save(OutboxEvent.builder()
                .outboxStatus(OutboxStatus.SENDING)
                .outboxEventType(OutboxEventType.NOTIFICATION)
                .aggregateId(event.id())
                .payload(jsonUtil.toJson(event))
                .build()
        );
        CorrelationData correlationData = new CorrelationData(String.valueOf(saved.getId()));
        //어떤 메시지에 대한 콜백인지 알기 위해 id 설정
        template.convertAndSend(RabbitKeys.MAIN_EXCHANGE, RabbitKeys.NOTIFICATION_ROUTING_KEY, event, correlationData);
    }
}
