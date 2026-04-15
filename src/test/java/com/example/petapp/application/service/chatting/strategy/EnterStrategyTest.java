package com.example.petapp.application.service.chatting.strategy;

import com.example.petapp.application.in.chatting.model.dto.NotificationDto;
import com.example.petapp.application.in.chatting.model.dto.SendResponseDto;
import com.example.petapp.application.in.chatting.model.type.CommandType;
import com.example.petapp.application.out.SendPort;
import com.example.petapp.domain.chatting.model.ChatMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EnterStrategyTest {

    @Mock
    private SendPort port;

    @InjectMocks
    private EnterStrategy enterStrategy;

    @Test
    void 입장메시지를_브로드캐스트한다() {
        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoomId(1L)
                .senderId(2L)
                .senderName("몽이")
                .build();

        enterStrategy.handle(chatMessage);

        ArgumentCaptor<SendResponseDto> captor = ArgumentCaptor.forClass(SendResponseDto.class);
        verify(port).send(org.mockito.ArgumentMatchers.eq("/sub/chat/1"), captor.capture());
        SendResponseDto response = captor.getValue();
        assertThat(response.getCommandType()).isEqualTo(CommandType.ENTER);
        NotificationDto body = (NotificationDto) response.getBody();
        assertThat(body.getUserId()).isEqualTo(2L);
        assertThat(body.getMessage()).isEqualTo("몽이님이 입장하셨습니다.");
    }
}
