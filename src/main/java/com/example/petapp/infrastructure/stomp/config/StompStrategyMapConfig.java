package com.example.petapp.infrastructure.stomp.config;

import com.example.petapp.infrastructure.stomp.strategy.StompCommandStrategy;
import com.example.petapp.infrastructure.stomp.strategy.impl.ConnectStrategy;
import com.example.petapp.infrastructure.stomp.strategy.impl.SendStrategy;
import com.example.petapp.infrastructure.stomp.strategy.impl.SubscribeStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.stomp.StompCommand;

import java.util.EnumMap;
import java.util.Map;

@Configuration
public class StompStrategyMapConfig {

    @Bean("commandStrategyMap")
    public Map<StompCommand, StompCommandStrategy> commandStrategyMap(
            ConnectStrategy connectStrategy,
            SubscribeStrategy subscribeStrategy,
            SendStrategy sendStrategy) {

        Map<StompCommand, StompCommandStrategy> map = new EnumMap<>(StompCommand.class);
        map.put(StompCommand.CONNECT, connectStrategy);
        map.put(StompCommand.SUBSCRIBE, subscribeStrategy);
        map.put(StompCommand.SEND, sendStrategy);

        return map;
    }
}
