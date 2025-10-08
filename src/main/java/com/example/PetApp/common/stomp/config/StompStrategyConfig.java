package com.example.PetApp.common.stomp.config;

import com.example.PetApp.common.stomp.strategy.command.StompCommandStrategy;
import com.example.PetApp.common.stomp.strategy.command.impl.ConnectStrategy;
import com.example.PetApp.common.stomp.strategy.command.impl.SendStrategy;
import com.example.PetApp.common.stomp.strategy.command.impl.SubscribeStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.stomp.StompCommand;

import java.util.EnumMap;
import java.util.Map;

@Configuration
public class StompStrategyConfig {

    @Bean("strategyMap")
    public Map<StompCommand, StompCommandStrategy> strategyMap(
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
