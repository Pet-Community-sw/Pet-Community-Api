package com.example.petapp.infrastructure.mq.consumer;

import com.example.petapp.infrastructure.mq.RabbitConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitRePublishConsumer {

    private final RabbitTemplate rabbitTemplate;

    /**
     * 재발행 큐
     */
    @RabbitListener(queues = RabbitConfig.REPUBLISH_QUEUE)
    public void handle(Message message) {
        String origRoutingKey = (String) message.getMessageProperties().getHeaders().get("x-orig-routing-key");

        rabbitTemplate.send(RabbitConfig.MAIN_EXCHANGE, origRoutingKey, message);
    }
}
