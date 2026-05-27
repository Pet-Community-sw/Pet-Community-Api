package com.example.petapp.interfaces.exception.stomp;

import com.example.petapp.application.common.JsonUtil;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import java.nio.charset.StandardCharsets;

@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class StompGlobalExceptionHandler {

    private final JsonUtil jsonUtil;

    @Bean
    public StompSubProtocolErrorHandler stompSubProtocolErrorHandler() {
        return new StompSubProtocolErrorHandler() {
            @Override
            public @Nullable Message<byte[]> handleClientMessageProcessingError(@Nullable Message<byte[]> clientMessage, Throwable ex) {
                Throwable cause = ex.getCause() != null ? ex.getCause() : ex;

                StompErrorResponse response = StompErrorResponse.from(cause);

                StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
                accessor.setMessage(response.getMessage());
                accessor.setLeaveMutable(true);

                byte[] payload = jsonUtil.toJson(response).getBytes(StandardCharsets.UTF_8);
                return MessageBuilder.createMessage(payload, accessor.getMessageHeaders());
            }
        };
    }
}
