package com.example.petapp.infrastructure.database.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
@RequiredArgsConstructor
public class RedisSubscriberConfig {

    private static final String NOTIFICATION_CHANNEL = "notification-channel";
    private final RedisConnectionFactory redisConnectionFactory;
    private final NotificationSubscriber notificationSubscriber;

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);

        container.addMessageListener(
                notificationSubscriber,
                new PatternTopic(NOTIFICATION_CHANNEL)
        );

        return container;
    }
}