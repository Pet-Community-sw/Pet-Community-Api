package com.example.petapp.application.service.chatting.strategy;

import com.example.petapp.application.in.chatroom.ChatRoomUseCase;
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
class LeaveStrategyTest {

    @Mock
    private ChatRoomUseCase chatRoomUseCase;
    @Mock
    private SendPort port;

    @InjectMocks
    private LeaveStrategy leaveStrategy;

    @Test
    void 퇴장메시지_전송후_채팅방에서_제거한다() {
        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoomId(3L)
                .senderId(4L)
                .senderName("철수")
                .build();

        leaveStrategy.handle(chatMessage);

        ArgumentCaptor<SendResponseDto> captor = ArgumentCaptor.forClass(SendResponseDto.class);
        verify(port).send(org.mockito.ArgumentMatchers.eq("/sub/chat/3"), captor.capture());
        SendResponseDto response = captor.getValue();
        assertThat(response.getCommandType()).isEqualTo(CommandType.LEAVE);
        NotificationDto body = (NotificationDto) response.getBody();
        assertThat(body.getUserId()).isEqualTo(4L);
        assertThat(body.getMessage()).isEqualTo("철수님이 나가셨습니다.");
        verify(chatRoomUseCase).deleteChatRoom(3L, 4L);
    }
}
