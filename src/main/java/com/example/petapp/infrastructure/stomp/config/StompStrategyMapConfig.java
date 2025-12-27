package com.example.petapp.infrastructure.stomp.config;

import com.example.petapp.infrastructure.stomp.strategy.command.StompCommandStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.stomp.StompCommand;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class StompStrategyMapConfig {
   
    @Bean("commandStrategyMap")
    public Map<StompCommand, StompCommandStrategy> commandStrategyMap(List<StompCommandStrategy> strategies) {
        Map<StompCommand, StompCommandStrategy> map = new HashMap<>();
        for (StompCommandStrategy strategy : strategies) {
            map.put(strategy.getCommand(), strategy);
        }
        return map;
    }

}
