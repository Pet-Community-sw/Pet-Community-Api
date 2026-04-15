package com.example.petapp.application.service.chatting.strategy;

import com.example.petapp.application.in.chatting.model.type.CommandType;
import com.example.petapp.domain.chatting.model.ChatMessage;
import com.example.petapp.infrastructure.database.adapter.AckInfoRepositoryAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AckStrategyTest {

    @Mock
    private AckInfoRepositoryAdapter ackInfoRepositoryAdapter;

    @InjectMocks
    private AckStrategy ackStrategy;

    @Test
    void ack메시지를_받으면_해당유저를_ack목록에서_제거한다() {
        ChatMessage chatMessage = ChatMessage.builder()
                .clientMessageId("client-1")
                .senderId(5L)
                .build();

        ackStrategy.handle(chatMessage);

        verify(ackInfoRepositoryAdapter).deleteUser("client-1", 5L);
        assertThat(ackStrategy.getCommand()).isEqualTo(CommandType.ACK);
    }
}
