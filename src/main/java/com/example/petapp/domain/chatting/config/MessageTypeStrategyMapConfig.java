package com.example.petapp.domain.chatting.config;

import com.example.petapp.domain.chatting.model.type.CommandType;
import com.example.petapp.domain.chatting.strategy.MessageTypeStrategy;
import com.example.petapp.domain.chatting.strategy.impl.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class MessageTypeStrategyMapConfig {

    @Bean(name = "messageTypeMap")
    public Map<CommandType, MessageTypeStrategy> messageTypeMap(
            EnterStrategy enterStrategy,
            TalkStrategy talkStrategy,
            ReadStrategy readStrategy,
            LeaveStrategy leaveStrategy,
            AckStrategy ackStrategy
    ) {
        Map<CommandType, MessageTypeStrategy> map = new HashMap<>();
        map.put(CommandType.ENTER, enterStrategy);
        map.put(CommandType.TALK, talkStrategy);
        map.put(CommandType.READ, readStrategy);
        map.put(CommandType.LEAVE, leaveStrategy);
        map.put(CommandType.ACK, ackStrategy);

        return map;
    }
}
