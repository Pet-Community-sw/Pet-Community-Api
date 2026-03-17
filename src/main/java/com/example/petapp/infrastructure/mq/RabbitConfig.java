package com.example.petapp.infrastructure.mq;

import com.example.petapp.application.in.outbox.OutboxEventUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.example.petapp.infrastructure.mq.RabbitKeys.*;

@RequiredArgsConstructor
@Configuration
public class RabbitConfig {

    public final OutboxEventUseCase useCase;

    //cdc로 변경
//    @Bean
//    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
//        RabbitTemplate template = new RabbitTemplate(connectionFactory);
//
//        template.setMessageConverter(messageConverter());
//        template.setConfirmCallback(((correlationData, ack, cause) -> {
//            if (correlationData == null) return;
//
//            Long outboxId = Long.valueOf(correlationData.getId());
//
//            useCase.update(outboxId, ack ? OutboxStatus.COMPLETED : OutboxStatus.PENDING);
//        }));
//        return template;
//    }

    //Exchange
    @Bean
    DirectExchange mainExchange() {
        return new DirectExchange(MAIN_EXCHANGE);
    }

    @Bean
    HeadersExchange retryExchange() {
        return new HeadersExchange(RETRY_EXCHANGE);
    }

    @Bean
    DirectExchange dlxExchange() {
        return new DirectExchange(DLX_EXCHANGE);
    }

    //Queue
    @Bean
    public Queue mailQueue() {
        return QueueBuilder.durable(MAIL_QUEUE).build();
    }

    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(NOTIFICATION_QUEUE).build();
    }

    @Bean
    public Queue elasticQueue() {
        return QueueBuilder.durable(MEMBER_QUEUE).build();
    }

    @Bean
    public Queue retry5sQueue() {
        return QueueBuilder.durable(RETRY_5S_QUEUE)
                .deadLetterExchange(MAIN_EXCHANGE)
                .ttl(5000)
                .build();
    }

    @Bean
    public Queue retry30sQueue() {
        return QueueBuilder.durable(RETRY_30S_QUEUE)
                .deadLetterExchange(MAIN_EXCHANGE)
                .ttl(30000)
                .build();
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DEAD_LETTER_QUEUE).build();
    }

    //Binding
    @Bean
    public Binding mailBinding() {
        return BindingBuilder.bind(mailQueue()).to(mainExchange()).with(MAIL_ROUTING_KEY);
    }

    @Bean
    public Binding notificationBinding() {
        return BindingBuilder.bind(notificationQueue()).to(mainExchange()).with(NOTIFICATION_ROUTING_KEY);
    }

    //ES 바인딩
    @Bean
    public Binding elasticBinding() {
        return BindingBuilder.bind(elasticQueue()).to(mainExchange()).with(MEMBER_ROUTING_KEY);
    }

    @Bean
    public Binding retry5sBinding() {
        return BindingBuilder.bind(retry5sQueue()).to(retryExchange()).where("retry-type").matches(RETRY_5S_ROUTING_KEY);
    }

    @Bean
    public Binding retry30sBinding() {
        return BindingBuilder.bind(retry30sQueue()).to(retryExchange()).where("retry-type").matches(RETRY_30S_ROUTING_KEY);
    }

    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(deadLetterQueue()).to(dlxExchange()).with(DEAD_LETTER_ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
