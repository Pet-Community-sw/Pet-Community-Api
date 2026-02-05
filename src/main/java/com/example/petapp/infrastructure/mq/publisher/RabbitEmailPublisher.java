package com.example.petapp.infrastructure.mq.publisher;

import com.example.petapp.application.common.JsonUtil;
import com.example.petapp.application.in.email.EventEmail;
import com.example.petapp.application.in.outbox.OutboxEventUseCase;
import com.example.petapp.application.out.EmailSendPort;
import com.example.petapp.domain.outboxevent.model.OutboxEvent;
import com.example.petapp.domain.outboxevent.model.OutboxEventType;
import com.example.petapp.domain.outboxevent.model.OutboxStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import static com.example.petapp.infrastructure.mq.RabbitKeys.MAIL_ROUTING_KEY;
import static com.example.petapp.infrastructure.mq.RabbitKeys.MAIN_EXCHANGE;

@Component
@RequiredArgsConstructor
public class RabbitEmailPublisher implements EmailSendPort {

    private final RabbitTemplate template;
    private final OutboxEventUseCase useCase;
    private final JsonUtil jsonUtil;

    @Override
    public void send(EventEmail event) {
        OutboxEvent saved = useCase.save(OutboxEvent.builder()
                .outboxStatus(OutboxStatus.SENDING)
                .outboxEventType(OutboxEventType.EMAIL)
                .aggregateId(event.id())
                .payload(jsonUtil.toJson(event))
                .build()
        );
        CorrelationData correlationData = new CorrelationData(String.valueOf(saved.getId()));
        //어떤 메시지에 대한 콜백인지 알기 위해 id 설정
        template.convertAndSend(MAIN_EXCHANGE, MAIL_ROUTING_KEY, event, correlationData);
    }
}
