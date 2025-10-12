package com.example.PetApp.domain.chatting.config;

import com.example.PetApp.domain.chatting.model.type.MessageType;
import com.example.PetApp.domain.chatting.strategy.MessageTypeStrategy;
import com.example.PetApp.domain.chatting.strategy.impl.EnterStrategy;
import com.example.PetApp.domain.chatting.strategy.impl.LeaveStrategy;
import com.example.PetApp.domain.chatting.strategy.impl.ReadStrategy;
import com.example.PetApp.domain.chatting.strategy.impl.TalkStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class MessageTypeStrategyMapConfig {

    @Bean(name = "messageTypeMap")
    public Map<MessageType, MessageTypeStrategy> messageTypeMap(
            EnterStrategy enterStrategy,
            TalkStrategy talkStrategy,
            ReadStrategy readStrategy,
            LeaveStrategy leaveStrategy
    ) {
        Map<MessageType, MessageTypeStrategy> map = new HashMap<>();
        map.put(MessageType.ENTER, enterStrategy);
        map.put(MessageType.TALK, talkStrategy);
        map.put(MessageType.READ, readStrategy);
        map.put(MessageType.LEAVE, leaveStrategy);

        return map;
    }
}
