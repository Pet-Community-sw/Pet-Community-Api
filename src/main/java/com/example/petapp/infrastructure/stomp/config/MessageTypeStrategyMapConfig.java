package com.example.petapp.infrastructure.stomp.config;

import com.example.petapp.application.in.chatting.MessageTypeStrategy;
import com.example.petapp.application.in.chatting.model.type.CommandType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class MessageTypeStrategyMapConfig {
    @Bean(name = "messageTypeMap")
    public Map<CommandType, MessageTypeStrategy> messageTypeMap(List<MessageTypeStrategy> strategies) {
        Map<CommandType, MessageTypeStrategy> map = new HashMap<>();
        for (MessageTypeStrategy strategy : strategies) {
            map.put(strategy.getType(), strategy);
        }
        return map;
    }
}