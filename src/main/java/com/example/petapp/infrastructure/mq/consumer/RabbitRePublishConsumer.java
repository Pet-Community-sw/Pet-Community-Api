package com.example.petapp.infrastructure.mq.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import static com.example.petapp.infrastructure.mq.RabbitKeys.MAIN_EXCHANGE;
import static com.example.petapp.infrastructure.mq.RabbitKeys.REPUBLISH_QUEUE;

@Component
@RequiredArgsConstructor
public class RabbitRePublishConsumer {

    private final RabbitTemplate rabbitTemplate;

    /**
     * 재발행 큐
     */
    @RabbitListener(queues = REPUBLISH_QUEUE)
    public void handle(Message message) {
        String origRoutingKey = (String) message.getMessageProperties().getHeaders().get("x-orig-routing-key");

        rabbitTemplate.send(MAIN_EXCHANGE, origRoutingKey, message);
    }
}
