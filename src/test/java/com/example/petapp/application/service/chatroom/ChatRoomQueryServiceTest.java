package com.example.petapp.application.service.chatroom;

import com.example.petapp.domain.chatroom.ChatRoomRepository;
import com.example.petapp.domain.chatroom.model.ChatRoom;
import com.example.petapp.domain.walkingtogetherPost.model.WalkingTogetherPost;
import com.example.petapp.interfaces.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatRoomQueryServiceTest {

    @Mock
    private ChatRoomRepository repository;

    @InjectMocks
    private ChatRoomQueryService chatRoomQueryService;

    @Test
    void 채팅방이_존재하면_ID로_조회에_성공한다() {
        ChatRoom chatRoom = org.mockito.Mockito.mock(ChatRoom.class);
        when(repository.find(1L)).thenReturn(Optional.of(chatRoom));

        ChatRoom result = chatRoomQueryService.find(1L);

        assertThat(result).isSameAs(chatRoom);
        verify(repository).find(1L);
    }

    @Test
    void 채팅방이_없으면_ID조회에서_예외가_발생한다() {
        when(repository.find(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chatRoomQueryService.find(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("해당 채팅방은 없습니다.");
    }

    @Test
    void 모임게시글로_조회는_저장소_결과를_그대로_반환한다() {
        WalkingTogetherPost post = org.mockito.Mockito.mock(WalkingTogetherPost.class);
        ChatRoom chatRoom = org.mockito.Mockito.mock(ChatRoom.class);
        when(repository.find(post)).thenReturn(Optional.of(chatRoom));

        Optional<ChatRoom> result = chatRoomQueryService.find(post);

        assertThat(result).contains(chatRoom);
        verify(repository).find(post);
    }

    @Test
    void 채팅방_참여여부_조회는_저장소_결과를_반환한다() {
        when(repository.existAndContain(1L, 10L)).thenReturn(true);

        boolean result = chatRoomQueryService.isExist(1L, 10L);

        assertThat(result).isTrue();
        verify(repository).existAndContain(1L, 10L);
    }
}
