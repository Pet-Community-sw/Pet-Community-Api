package com.example.petapp.infrastructure.stomp.config;

import com.example.petapp.infrastructure.stomp.interceptor.StompInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@RequiredArgsConstructor
@EnableWebSocketMessageBroker
@Configuration(proxyBeanMethods = false)

public class StompConfig implements WebSocketMessageBrokerConfigurer {

    private final StompInterceptor stompInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/sub");//MessageBroker로 넘어감
//                .setTaskScheduler(taskScheduler())
//                .setHeartbeatValue(new long[]{3000L, 3000L});//하트비트설정(서버가 클라이언트에게 하트비트를 보내는 주기,서버가 클라이언트로부터 하트비트를 기대하는 주기)
        registry.setUserDestinationPrefix("/queue");
        registry.setApplicationDestinationPrefixes("/pub");//@MessageMapping으로 넘어감
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-stomp")
                .setAllowedOriginPatterns("*");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompInterceptor);
    }

}
