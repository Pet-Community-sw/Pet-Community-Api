package com.example.petapp.infrastructure.mq.consumer;

import com.example.petapp.application.in.notification.NotificationUseCase;
import com.example.petapp.application.in.notification.dto.NotificationEvent;
import com.example.petapp.infrastructure.mq.RabbitConfig;
import com.example.petapp.infrastructure.mq.RabbitRetryHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitNotificationConsumer {

    private final NotificationUseCase useCase;
    private final RabbitRetryHandler handler;

    @RabbitListener(queues = RabbitConfig.NOTIFICATION_QUEUE)
    public void handle(NotificationEvent event, Message message) {
        try {
            useCase.send(event);
        } catch (Exception e) {
            handler.handle(event, message, e);
        }
    }
}
