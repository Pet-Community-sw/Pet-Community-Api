package com.example.PetApp.infrastructure.redis;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories
@Getter
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    @Bean//알림을 위한 redisTemplate
    public RedisTemplate<String, Object> notificationRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> notificationRedisTemplate = new RedisTemplate<>();
        notificationRedisTemplate.setConnectionFactory(redisConnectionFactory);
        notificationRedisTemplate.setKeySerializer(new StringRedisSerializer());
        notificationRedisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        return notificationRedisTemplate;
    }

    @Bean//위치 저장을 위한 redisTemplate
    public RedisTemplate<String, Object> locationRedisTemplate() {
        RedisTemplate<String, Object> locationRedisTemplate = new RedisTemplate<>();
        locationRedisTemplate.setConnectionFactory(redisConnectionFactory());
        locationRedisTemplate.setKeySerializer(new StringRedisSerializer());
        locationRedisTemplate.setHashKeySerializer(new StringRedisSerializer());//해시 설정
        locationRedisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        locationRedisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        return locationRedisTemplate;
    }

    @Bean
    public RedisTemplate<String, Long> likeRedisTemplate() {
        RedisTemplate<String, Long> likeRedisTemplate = new RedisTemplate<>();
        likeRedisTemplate.setConnectionFactory(redisConnectionFactory());
        likeRedisTemplate.setKeySerializer(new StringRedisSerializer());
        likeRedisTemplate.setValueSerializer(new GenericToStringSerializer<>(Long.class));
        return likeRedisTemplate;
    }

    @Bean//topic 설정.
    public RedisMessageListenerContainer notificationRedisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory,
                                                                                   NotificationRedisSubscriber notificationRedisSubscriber) {
        RedisMessageListenerContainer notificationRedisMessageListenerContainer = new RedisMessageListenerContainer();
        notificationRedisMessageListenerContainer.setConnectionFactory(redisConnectionFactory);
        notificationRedisMessageListenerContainer.addMessageListener((message, pattern) ->
                        notificationRedisSubscriber.onMessage(
                                new String(message.getChannel()),
                                new String(message.getBody()))
                , new PatternTopic("member:*")
        );

        return notificationRedisMessageListenerContainer;
    }
}
