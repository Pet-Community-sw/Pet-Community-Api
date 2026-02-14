package com.example.petapp.application.listener;

import com.example.petapp.application.common.JsonUtil;
import com.example.petapp.application.in.member.object.MemberEvent;
import com.example.petapp.application.in.outbox.OutboxEventUseCase;
import com.example.petapp.domain.outboxevent.model.OutboxEvent;
import com.example.petapp.infrastructure.mq.RabbitKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberListener {

    private final OutboxEventUseCase useCase;
    private final JsonUtil jsonUtil;
    private final ApplicationEventPublisher publisher;

    @EventListener
    public void handle(MemberEvent event) {
        OutboxEvent outboxEvent = useCase.save(OutboxEvent.builder()
                        .exchangeKey(RabbitKeys.MAIN_EXCHANGE)
                        .routingKey(RabbitKeys.MEMBER_ROUTING_KEY)
//                      .outboxStatus(OutboxStatus.SENDING)
//                      .outboxEventType(OutboxEventType.MEMBER)
                        .aggregateId(event.getMemberId())
                        .payload(jsonUtil.toJson(event))
                        .build()
        );
        //mq 이벤트 발행
        publisher.publishEvent(outboxEvent);
    }
}
