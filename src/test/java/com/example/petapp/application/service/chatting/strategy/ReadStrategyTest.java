package com.example.petapp.application.service.chatting.strategy;

import com.example.petapp.application.in.chatting.model.type.CommandType;
import com.example.petapp.application.out.cache.ReadMessageCachePort;
import com.example.petapp.domain.chatting.model.ChatMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReadStrategyTest {

    @Mock
    private ReadMessageCachePort port;

    @InjectMocks
    private ReadStrategy readStrategy;

    @Test
    void 읽음메시지를_받으면_읽음캐시에_저장한다() {
        ChatMessage chatMessage = ChatMessage.builder().chatRoomId(1L).senderId(2L).build();

        readStrategy.handle(chatMessage);

        verify(port).create(chatMessage);
        assertThat(readStrategy.getCommand()).isEqualTo(CommandType.READ);
    }
}
