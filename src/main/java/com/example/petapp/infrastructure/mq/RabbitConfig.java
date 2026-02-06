package com.example.petapp.infrastructure.mq;

import com.example.petapp.application.in.outbox.OutboxEventUseCase;
import com.example.petapp.domain.outboxevent.model.OutboxStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.example.petapp.infrastructure.mq.RabbitKeys.*;

@RequiredArgsConstructor
@Configuration
public class RabbitConfig {

    public final OutboxEventUseCase useCase;

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);

        template.setMessageConverter(messageConverter());
        template.setConfirmCallback(((correlationData, ack, cause) -> {

            Long outboxId = Long.valueOf(correlationData.getId());
            if (ack) useCase.update(outboxId, OutboxStatus.COMPLETED);
            else useCase.update(outboxId, OutboxStatus.PENDING);
        }));
        return template;
    }

    //Exchange
    @Bean
    DirectExchange mainExchange() {
        return new DirectExchange(MAIN_EXCHANGE);
    }

    @Bean
    DirectExchange retryExchange() {
        return new DirectExchange(RETRY_EXCHANGE);
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
    public Queue wait5sQueue() {
        return QueueBuilder.durable(WAIT_5S_QUEUE)
                .withArgument("x-dead-letter-exchange", RETRY_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", REPUBLISH_ROUTING_KEY)
                .withArgument("x-message-ttl", 5000)
                .build();
    }

    @Bean
    public Queue wait30sQueue() {
        return QueueBuilder.durable(WAIT_30S_QUEUE)
                .withArgument("x-dead-letter-exchange", RETRY_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", REPUBLISH_ROUTING_KEY)
                .withArgument("x-message-ttl", 30000)
                .build();
    }

    /**
     * 딜레이 큐에서 메인큐의 원래 큐로 복귀할 때 사용하는 큐(서비스 별로 큐를 생성하지 않음)
     */
    @Bean
    public Queue republishQueue() {
        return QueueBuilder.durable(REPUBLISH_QUEUE)
                .withArgument("x-dead-letter-exchange", MAIN_EXCHANGE)
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
        return BindingBuilder.bind(wait5sQueue()).to(retryExchange()).with(RETRY_5S_ROUTING_KEY);
    }

    @Bean
    public Binding retry30sBinding() {
        return BindingBuilder.bind(wait30sQueue()).to(retryExchange()).with(RETRY_30S_ROUTING_KEY);
    }

    @Bean
    public Binding republishBinding() {
        return BindingBuilder.bind(republishQueue()).to(retryExchange()).with(REPUBLISH_ROUTING_KEY);
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
