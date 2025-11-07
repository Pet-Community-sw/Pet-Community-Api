package com.example.petapp.domain.chatting.config;

import com.example.petapp.domain.chatting.model.type.CommandType;
import com.example.petapp.domain.chatting.strategy.MessageTypeStrategy;
import com.example.petapp.domain.chatting.strategy.impl.EnterStrategy;
import com.example.petapp.domain.chatting.strategy.impl.LeaveStrategy;
import com.example.petapp.domain.chatting.strategy.impl.ReadStrategy;
import com.example.petapp.domain.chatting.strategy.impl.TalkStrategy;
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
            LeaveStrategy leaveStrategy
    ) {
        Map<CommandType, MessageTypeStrategy> map = new HashMap<>();
        map.put(CommandType.ENTER, enterStrategy);
        map.put(CommandType.TALK, talkStrategy);
        map.put(CommandType.READ, readStrategy);
        map.put(CommandType.LEAVE, leaveStrategy);

        return map;
    }
}
