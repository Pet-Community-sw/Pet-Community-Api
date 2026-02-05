package com.example.petapp.infrastructure.mq.publisher;

import com.example.petapp.application.in.email.EventEmail;
import com.example.petapp.application.out.EmailSendPort;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import static com.example.petapp.infrastructure.mq.RabbitKeys.MAIL_ROUTING_KEY;
import static com.example.petapp.infrastructure.mq.RabbitKeys.MAIN_EXCHANGE;

@Component
@RequiredArgsConstructor
public class RabbitEmailPublisher implements EmailSendPort {

    private final RabbitTemplate template;

    @Override
    public void send(EventEmail event) {
        template.convertAndSend(MAIN_EXCHANGE, MAIL_ROUTING_KEY, event);
    }
}
