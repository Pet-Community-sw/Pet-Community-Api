package com.example.petapp.application.service.chatting;

import com.example.petapp.application.in.chatroom.ChatRoomQueryUseCase;
import com.example.petapp.application.in.chatting.model.dto.SendResponseDto;
import com.example.petapp.application.in.chatting.model.dto.TypingMessageDto;
import com.example.petapp.application.in.chatting.model.type.CommandType;
import com.example.petapp.application.in.member.MemberQueryUseCase;
import com.example.petapp.application.out.SendPort;
import com.example.petapp.application.out.cache.TypingCachePort;
import com.example.petapp.domain.chatroom.model.ChatRoom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TypingIndicatorServiceTest {

    @Mock
    private SendPort sendPort;
    @Mock
    private ChatRoomQueryUseCase chatRoomQueryUseCase;
    @Mock
    private TypingCachePort typingCachePort;
    @Mock
    private MemberQueryUseCase memberQueryUseCase;

    @InjectMocks
    private TypingIndicatorService typingIndicatorService;

    @Test
    void 타이핑_시작시_캐시에_저장하고_브로드캐스트한다() {
        ChatRoom chatRoom = org.mockito.Mockito.mock(ChatRoom.class);
        when(chatRoomQueryUseCase.find(1L)).thenReturn(chatRoom);
        when(typingCachePort.getList(1L)).thenReturn(List.of(10L, 20L));
        when(memberQueryUseCase.findNamesOrThrowByIds(List.of(10L, 20L))).thenReturn(List.of("철수", "영희"));

        typingIndicatorService.sendTypingStatus(new TypingMessageDto(1L, true), 10L);

        verify(chatRoom).validateUser(10L);
        verify(typingCachePort).create(1L, 10L, 3000L);
        verify(typingCachePort, never()).delete(1L, 10L);

        ArgumentCaptor<SendResponseDto> captor = ArgumentCaptor.forClass(SendResponseDto.class);
        verify(sendPort).send(org.mockito.ArgumentMatchers.eq("/sub/chat/typing/1"), captor.capture());
        SendResponseDto dto = captor.getValue();
        assertThat(dto.getCommandType()).isEqualTo(CommandType.TYPING);
        assertThat((List<String>) dto.getBody()).containsExactly("철수", "영희");
    }

    @Test
    void 타이핑_종료시_캐시에서_삭제하고_브로드캐스트한다() {
        ChatRoom chatRoom = org.mockito.Mockito.mock(ChatRoom.class);
        when(chatRoomQueryUseCase.find(2L)).thenReturn(chatRoom);
        when(typingCachePort.getList(2L)).thenReturn(List.of());
        when(memberQueryUseCase.findNamesOrThrowByIds(List.of())).thenReturn(List.of());

        typingIndicatorService.sendTypingStatus(new TypingMessageDto(2L, false), 30L);

        verify(chatRoom).validateUser(30L);
        verify(typingCachePort).delete(2L, 30L);
        verify(typingCachePort, never()).create(2L, 30L, 3000L);
        verify(sendPort).send(org.mockito.ArgumentMatchers.eq("/sub/chat/typing/2"), org.mockito.ArgumentMatchers.any(SendResponseDto.class));
    }
}
