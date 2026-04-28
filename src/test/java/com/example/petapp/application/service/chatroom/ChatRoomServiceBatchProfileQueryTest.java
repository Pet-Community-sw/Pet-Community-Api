package com.example.petapp.application.service.chatroom;

import com.example.petapp.application.in.chatroom.ChatRoomUseCase;
import com.example.petapp.application.in.chatroom.dto.response.ChatRoomResponseDto;
import com.example.petapp.application.in.chatting.ReaderUseCase;
import com.example.petapp.application.in.chatting.model.dto.LastMessageInfoDto;
import com.example.petapp.application.in.chatting.model.type.ChatRoomType;
import com.example.petapp.application.in.profile.ProfileUseCase;
import com.example.petapp.application.out.cache.LastMessageCachePort;
import com.example.petapp.application.out.cache.ReadMessageCachePort;
import com.example.petapp.application.out.cache.SeqCachePort;
import com.example.petapp.domain.chatroom.ChatRoomRepository;
import com.example.petapp.domain.chatroom.model.ChatRoom;
import com.example.petapp.domain.chatting.ChatMessageRepository;
import com.example.petapp.domain.profile.model.Profile;
import com.example.petapp.domain.walkingtogetherPost.model.WalkingTogetherPost;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatRoomServiceBatchProfileQueryTest {

    @Mock
    private ProfileUseCase profileUseCase;
    @Mock
    private ChatRoomRepository chatRoomRepository;
    @Mock
    private ChatMessageRepository chatMessageRepository;
    @Mock
    private ReaderUseCase readerUseCase;
    @Mock
    private ChatRoomUseCase chatRoomUseCase;
    @Mock
    private SeqCachePort seqCachePort;
    @Mock
    private ReadMessageCachePort readMessageCachePort;
    @Mock
    private LastMessageCachePort lastMessageCachePort;

    @InjectMocks
    private ChatRoomService chatRoomService;

    @Test
    void 채팅방_목록조회시_프로필은_한번에_조회한다() {
        ChatRoom chatRoom1 = org.mockito.Mockito.mock(ChatRoom.class);
        ChatRoom chatRoom2 = org.mockito.Mockito.mock(ChatRoom.class);
        WalkingTogetherPost walkingTogetherPost1 = org.mockito.Mockito.mock(WalkingTogetherPost.class);
        WalkingTogetherPost walkingTogetherPost2 = org.mockito.Mockito.mock(WalkingTogetherPost.class);
        Profile owner1 = org.mockito.Mockito.mock(Profile.class);
        Profile owner2 = org.mockito.Mockito.mock(Profile.class);
        Profile p1 = org.mockito.Mockito.mock(Profile.class);
        Profile p2 = org.mockito.Mockito.mock(Profile.class);
        Profile p3 = org.mockito.Mockito.mock(Profile.class);

        when(chatRoomRepository.findAll(1L, ChatRoomType.MANY)).thenReturn(List.of(chatRoom1, chatRoom2));

        when(chatRoom1.getId()).thenReturn(100L);
        when(chatRoom1.getName()).thenReturn("채팅방1");
        when(chatRoom1.getUsers()).thenReturn(Set.of(1L, 2L));
        when(chatRoom1.getWalkingTogetherPost()).thenReturn(walkingTogetherPost1);
        when(walkingTogetherPost1.getProfile()).thenReturn(owner1);
        when(owner1.getId()).thenReturn(1L);

        when(chatRoom2.getId()).thenReturn(200L);
        when(chatRoom2.getName()).thenReturn("채팅방2");
        when(chatRoom2.getUsers()).thenReturn(Set.of(2L, 3L));
        when(chatRoom2.getWalkingTogetherPost()).thenReturn(walkingTogetherPost2);
        when(walkingTogetherPost2.getProfile()).thenReturn(owner2);
        when(owner2.getId()).thenReturn(2L);

        when(readMessageCachePort.find(100L, 1L)).thenReturn(2L);
        when(readMessageCachePort.find(200L, 1L)).thenReturn(4L);
        when(lastMessageCachePort.find(100L)).thenReturn(LastMessageInfoDto.builder()
                .lastSeq(5L)
                .lastMessage("메시지1")
                .lastMessageTime("2026-01-01T00:00:00")
                .build());
        when(lastMessageCachePort.find(200L)).thenReturn(LastMessageInfoDto.builder()
                .lastSeq(8L)
                .lastMessage("메시지2")
                .lastMessageTime("2026-01-01T00:01:00")
                .build());

        when(profileUseCase.findMapOrThrow(Set.of(1L, 2L, 3L))).thenReturn(Map.of(
                1L, p1,
                2L, p2,
                3L, p3
        ));
        when(p1.getId()).thenReturn(1L);
        when(p1.getPetImageUrl()).thenReturn("1.png");
        when(p2.getId()).thenReturn(2L);
        when(p2.getPetImageUrl()).thenReturn("2.png");
        when(p3.getId()).thenReturn(3L);
        when(p3.getPetImageUrl()).thenReturn("3.png");

        List<ChatRoomResponseDto> result = chatRoomService.getChatRooms(1L);

        assertThat(result).hasSize(2);
        verify(profileUseCase).findMapOrThrow(Set.of(1L, 2L, 3L));
        verify(profileUseCase, never()).findOrThrow(anyLong());
    }

    @Test
    void 채팅방이_없으면_프로필_배치조회는_호출하지_않는다() {
        when(chatRoomRepository.findAll(1L, ChatRoomType.MANY)).thenReturn(List.of());

        List<ChatRoomResponseDto> result = chatRoomService.getChatRooms(1L);

        assertThat(result).isEmpty();
        verify(profileUseCase, never()).findMapOrThrow(org.mockito.ArgumentMatchers.anySet());
    }
}
