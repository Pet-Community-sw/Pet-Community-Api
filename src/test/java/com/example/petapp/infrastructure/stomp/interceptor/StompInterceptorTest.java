package com.example.petapp.infrastructure.stomp.interceptor;

import com.example.petapp.infrastructure.stomp.strategy.command.StompCommandStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StompInterceptorTest {

    @Mock
    private MessageChannel channel;
    @Mock
    private StompCommandStrategy strategy;

    @Test
    void stomp_accessor가_없으면_메시지를_그대로_반환한다() {
        StompInterceptor interceptor = new StompInterceptor(Map.of());
        Message<String> message = MessageBuilder.withPayload("plain").build();

        Message<?> result = interceptor.preSend(message, channel);

        assertThat(result).isSameAs(message);
    }

    @Test
    void command에_해당하는_전략이_있으면_handle을_호출한다() {
        StompInterceptor interceptor = new StompInterceptor(Map.of(StompCommand.SEND, strategy));
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SEND);
        accessor.setDestination("/pub/chat");
        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

        Message<?> result = interceptor.preSend(message, channel);

        assertThat(result).isSameAs(message);
        verify(strategy).handle(any(StompHeaderAccessor.class));
    }

    @Test
    void 지원하지_않는_command면_예외가_발생한다() {
        StompInterceptor interceptor = new StompInterceptor(Map.of());
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SEND);
        accessor.setDestination("/pub/chat");
        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

        assertThatThrownBy(() -> interceptor.preSend(message, channel))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지원하지 않는 command");
    }
}
