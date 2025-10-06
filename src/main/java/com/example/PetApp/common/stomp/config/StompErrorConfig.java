package com.example.PetApp.common.stomp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

import java.nio.charset.StandardCharsets;

@Configuration
public class StompErrorConfig {

    @Bean
    public StompSubProtocolErrorHandler stompSubProtocolErrorHandler() {
        return new StompSubProtocolErrorHandler() {
            @Override
            public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {
                StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
                accessor.setMessage(ex.getMessage());      // headers[message]
                accessor.setLeaveMutable(true);
                byte[] payload = (ex.getMessage() == null ? "" : ex.getMessage()).getBytes(StandardCharsets.UTF_8);
                return MessageBuilder.createMessage(payload, accessor.getMessageHeaders());
            }
        };
    }
}

