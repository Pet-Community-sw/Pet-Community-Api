package com.example.petapp.infrastructure.mq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String MAIN_EXCHANGE = "ex.main";
    public static final String RETRY_EXCHANGE = "ex.retry";
    public static final String DLX_EXCHANGE = "ex.dlx";

    public static final String MAIL_QUEUE = "q.mail";
    public static final String NOTIFICATION_QUEUE = "q.notification";

    public static final String WAIT_5S_QUEUE = "q.wait.5s";
    public static final String WAIT_30S_QUEUE = "q.wait.30s";
    public static final String DEAD_LETTER_QUEUE = "q.dead.letter";
    public static final String REPUBLISH_QUEUE = "q.republish";

    public static final String MAIL_ROUTING_KEY = "mail.key";
    public static final String NOTIFICATION_ROUTING_KEY = "notification.key";

    // --- Exchange 빈 등록 ---
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

    // --- 메인 큐 등록 (메일, 알림 등) ---
    @Bean
    public Queue mailQueue() {
        return QueueBuilder.durable(MAIL_QUEUE).build();
    }

    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(NOTIFICATION_QUEUE).build();
    }

    @Bean
    public Queue wait5sQueue() {
        return QueueBuilder.durable(WAIT_5S_QUEUE)
                .withArgument("x-dead-letter-exchange", RETRY_EXCHANGE)//만료 시 메인으로 복귀
                .withArgument("x-dead-letter-routing-key", "republish")
                .withArgument("x-message-ttl", 5000)
                .build();
    }

    @Bean
    public Queue wait30sQueue() {
        return QueueBuilder.durable(WAIT_30S_QUEUE)
                .withArgument("x-dead-letter-exchange", RETRY_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", "republish")
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

    @Bean
    public Binding mailBinding() {
        return BindingBuilder.bind(mailQueue()).to(mainExchange()).with(MAIL_ROUTING_KEY);
    }

    @Bean
    public Binding notificationBinding() {
        return BindingBuilder.bind(notificationQueue()).to(mainExchange()).with(NOTIFICATION_ROUTING_KEY);
    }

    // "5s"라는 키로 던지면 5초 대기 큐로 감
    @Bean
    public Binding retry5sBinding() {
        return BindingBuilder.bind(wait5sQueue()).to(retryExchange()).with("5s");
    }

    @Bean
    public Binding retry30sBinding() {
        return BindingBuilder.bind(wait30sQueue()).to(retryExchange()).with("30s");
    }

    @Bean
    public Binding republishBinding() {
        return BindingBuilder.bind(republishQueue()).to(retryExchange()).with("republish");
    }

    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(deadLetterQueue()).to(dlxExchange()).with("dead.letter");
    }

    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
