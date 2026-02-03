package com.example.petapp.infrastructure.mq;

import com.example.petapp.application.in.email.EventEmail;
import com.example.petapp.application.out.EmailSendPort;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailAdapter implements EmailSendPort {

    private final RabbitTemplate template;

    @Override
    public void send(EventEmail event) {
        template.convertAndSend(RabbitConfig.MAIN_EXCHANGE, RabbitConfig.MAIL_ROUTING_KEY, event);
    }
}
