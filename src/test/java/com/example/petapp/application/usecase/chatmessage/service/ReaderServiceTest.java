package com.example.petapp.application.usecase.chatmessage.service;

import com.example.petapp.application.out.cache.LastMessageCachePort;
import com.example.petapp.application.out.cache.ReadMessageCachePort;
import com.example.petapp.application.usecase.chatmessage.model.dto.LastMessageInfoDto;
import com.example.petapp.application.usecase.chatroom.ChatRoomQueryUseCase;
import com.example.petapp.domain.chatmessage.ChatMessageRepository;
import com.example.petapp.domain.chatroom.model.ChatRoom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReaderServiceTest {

    @Mock
    private ChatMessageRepository chatMessageRepository;
    @Mock
    private ChatRoomQueryUseCase chatRoomQueryUseCase;
    @Mock
    private ReadMessageCachePort readMessageCachePort;
    @Mock
    private LastMessageCachePort lastMessageCachePort;

    @InjectMocks
    private ReaderService readerService;

    @Test
    void 메세지조회시_마지막메세지조회는_채팅방ID를_사용한다() {
        Long chatRoomId = 1L;
        Long userId = 2L;

        ChatRoom chatRoom = org.mockito.Mockito.mock(ChatRoom.class);
        when(chatRoomQueryUseCase.find(chatRoomId)).thenReturn(chatRoom);
        when(chatMessageRepository.findAll(eq(chatRoomId), any(Pageable.class))).thenReturn(Page.empty());
        when(lastMessageCachePort.find(chatRoomId)).thenReturn(
                LastMessageInfoDto.builder()
                        .lastSeq(10L)
                        .lastMessage("")
                        .lastMessageTime("")
                        .build()
        );

        readerService.getMessages(chatRoomId, userId, 0);

        verify(lastMessageCachePort).find(chatRoomId);
        verify(lastMessageCachePort, never()).find(userId);
        verify(readMessageCachePort).create(argThat(chatMessage ->
                chatMessage.getChatRoomId().equals(chatRoomId)
                        && chatMessage.getSenderId().equals(userId)
                        && chatMessage.getSeq().equals(10L)
        ));
    }
}
