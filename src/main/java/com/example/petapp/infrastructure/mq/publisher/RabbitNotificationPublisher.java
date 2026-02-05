package com.example.petapp.infrastructure.mq.publisher;

import com.example.petapp.application.in.notification.dto.NotificationEvent;
import com.example.petapp.infrastructure.mq.RabbitKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitNotificationPublisher {

    public final RabbitTemplate template;

    @EventListener
    public void handle(NotificationEvent event) {
        template.convertAndSend(RabbitKeys.MAIN_EXCHANGE, RabbitKeys.NOTIFICATION_ROUTING_KEY, event);
    }
}
