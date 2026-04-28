package com.example.petapp.application.service.chatting;

import com.example.petapp.application.in.chatroom.ChatRoomUseCase;
import com.example.petapp.application.in.chatting.model.dto.LastMessageInfoDto;
import com.example.petapp.application.out.SendPort;
import com.example.petapp.application.out.cache.LastMessageCachePort;
import com.example.petapp.application.out.cache.ReadMessageCachePort;
import com.example.petapp.domain.chatroom.model.ChatRoom;
import com.example.petapp.domain.chatting.ChatMessageRepository;
import com.example.petapp.infrastructure.database.mongo.MongoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReaderServiceTest {

    @Mock
    private ChatMessageRepository chatMessageRepository;
    @Mock
    private ChatRoomUseCase chatRoomUseCase;
    @Mock
    private ObjectProvider<ChatRoomUseCase> chatRoomUseCaseProvider;
    @Mock
    private SendPort sendPort;
    @Mock
    private MongoService mongoService;
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
        when(chatRoomUseCaseProvider.getObject()).thenReturn(chatRoomUseCase);
        when(chatRoomUseCase.find(chatRoomId)).thenReturn(chatRoom);
        when(chatMessageRepository.findAll(eq(chatRoomId), any(Pageable.class))).thenReturn(Page.empty());
        when(readMessageCachePort.find(chatRoomId, userId)).thenReturn(3L);
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
        verify(mongoService).updateMessages(chatRoomId, userId, 3L, 10L);
    }
}
