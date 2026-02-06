package com.example.petapp.infrastructure.mq.producer;

import com.example.petapp.domain.outboxevent.model.OutboxEvent;
import com.example.petapp.infrastructure.mq.RabbitKeys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMessageProducer {

    private final RabbitTemplate template;

    //outbox 커밋 이후에 실행
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publish(OutboxEvent event) {
        CorrelationData correlationData = new CorrelationData(String.valueOf(event.getId()));
        //어떤 메시지에 대한 콜백인지 알기 위해 id 설정

        String routingKey = switch (event.getOutboxEventType()) {
            case MEMBER -> RabbitKeys.MEMBER_ROUTING_KEY;
            case EMAIL -> RabbitKeys.MAIL_ROUTING_KEY;
            case NOTIFICATION -> RabbitKeys.NOTIFICATION_ROUTING_KEY;
        };

        template.convertAndSend(RabbitKeys.MAIN_EXCHANGE, routingKey, event, correlationData);

    }
}
