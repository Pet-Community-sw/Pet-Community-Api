package com.example.petapp.infrastructure.database.cache.out.redis;

import com.example.petapp.domain.notification.model.dto.NotificationListDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
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

    /**
     * 공통 ObjectMapper: JavaTimeModule 등록 + 타임스탬프 비활성화
     */
    private ObjectMapper jacksonMapper() {
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return om;
    }

    @Bean//알림을 위한 redisTemplate
    public RedisTemplate<String, NotificationListDto> notificationRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        //LocalDateTime 지원하도록 ObjectMapper 주입
        Jackson2JsonRedisSerializer<NotificationListDto> valSer =
                new Jackson2JsonRedisSerializer<>(NotificationListDto.class);
        valSer.setObjectMapper(jacksonMapper());

        RedisTemplate<String, NotificationListDto> notificationRedisTemplate = new RedisTemplate<>();
        notificationRedisTemplate.setConnectionFactory(redisConnectionFactory);
        notificationRedisTemplate.setKeySerializer(new StringRedisSerializer());
        notificationRedisTemplate.setHashKeySerializer(new StringRedisSerializer());
        notificationRedisTemplate.setValueSerializer(valSer);
        notificationRedisTemplate.setHashValueSerializer(valSer);
        notificationRedisTemplate.afterPropertiesSet();
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
}
