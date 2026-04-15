package com.example.petapp.infrastructure.stomp;

import com.example.petapp.application.in.chatting.model.dto.SendResponseDto;
import com.example.petapp.application.in.chatting.model.type.CommandType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StompAdapterTest {

    @Mock
    private SimpMessagingTemplate template;

    @InjectMocks
    private StompAdapter stompAdapter;

    @Test
    void send는_convertAndSend를_호출한다() {
        SendResponseDto<String> dto = SendResponseDto.<String>builder()
                .commandType(CommandType.TALK)
                .body("body")
                .build();

        stompAdapter.send("/sub/chat/1", dto);

        verify(template).convertAndSend("/sub/chat/1", dto);
    }

    @Test
    void sendToUser는_convertAndSendToUser를_호출한다() {
        SendResponseDto<String> dto = SendResponseDto.<String>builder()
                .commandType(CommandType.TALK)
                .body("body")
                .build();

        stompAdapter.sendToUser("1", "/sub/chat", dto);

        verify(template).convertAndSendToUser("1", "/sub/chat", dto);
    }
}
