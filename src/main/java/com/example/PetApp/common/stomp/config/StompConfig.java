package com.example.PetApp.common.stomp.config;

import com.example.PetApp.common.stomp.interceptor.StompInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@RequiredArgsConstructor
@EnableWebSocketMessageBroker
@Configuration
public class StompConfig implements WebSocketMessageBrokerConfigurer {

    private final StompInterceptor stompInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/sub");//MessageBroker로 넘어감
//                .setTaskScheduler(taskScheduler())
//                .setHeartbeatValue(new long[]{3000L, 3000L});//하트비트설정(서버가 클라이언트에게 하트비트를 보내는 주기,서버가 클라이언트로부터 하트비트를 기대하는 주기)
        registry.setUserDestinationPrefix("/user");
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

//    private ThreadPoolTaskScheduler taskScheduler() {//스케줄링 작업을 처리할 쓰레드 풀
//        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
//        scheduler.setPoolSize(1);//풀 크기를 설정 하나로 하트비트를 주기적으로 처리함.
//        scheduler.setThreadNamePrefix("wss-heartbeat-");//로그 어떤 스레드가 하트비트를 보내는지 구분
//        scheduler.initialize();//스케줄러 인스턴스를 초기화해서 실행 상태로 전환.
//        return scheduler;
//    } 프론트와 협의 후 진행 결정
}
