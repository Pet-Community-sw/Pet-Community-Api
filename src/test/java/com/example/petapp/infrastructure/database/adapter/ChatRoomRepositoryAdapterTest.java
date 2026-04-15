package com.example.petapp.infrastructure.database.adapter;

import com.example.petapp.application.in.chatting.model.type.ChatRoomType;
import com.example.petapp.domain.chatroom.model.ChatRoom;
import com.example.petapp.domain.walkingtogetherPost.model.WalkingTogetherPost;
import com.example.petapp.infrastructure.database.jpa.chatroom.JpaChatRoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatRoomRepositoryAdapterTest {

    @Mock
    private JpaChatRoomRepository repository;

    @InjectMocks
    private ChatRoomRepositoryAdapter adapter;

    @Test
    void countByProfile은_저장소에_위임한다() {
        when(repository.countByProfile(1L)).thenReturn(2);

        int result = adapter.countByProfile(1L);

        assertThat(result).isEqualTo(2);
    }

    @Test
    void findAll은_유저와_타입으로_조회한다() {
        List<ChatRoom> list = List.of(ChatRoom.builder().build());
        when(repository.findAllByUserIdAndChatRoomType(1L, ChatRoomType.MANY)).thenReturn(list);

        List<ChatRoom> result = adapter.findAll(1L, ChatRoomType.MANY);

        assertThat(result).isSameAs(list);
    }

    @Test
    void find는_모임게시글로_조회한다() {
        WalkingTogetherPost post = org.mockito.Mockito.mock(WalkingTogetherPost.class);
        ChatRoom chatRoom = ChatRoom.builder().build();
        when(repository.findByWalkingTogetherPost(post)).thenReturn(Optional.of(chatRoom));

        Optional<ChatRoom> result = adapter.find(post);

        assertThat(result).contains(chatRoom);
    }

    @Test
    void existAndContain은_존재여부조회에_위임한다() {
        when(repository.existsByIdAndUsersContains(1L, 2L)).thenReturn(true);

        boolean result = adapter.existAndContain(1L, 2L);

        assertThat(result).isTrue();
    }

    @Test
    void save_delete_find를_저장소에_위임한다() {
        ChatRoom chatRoom = ChatRoom.builder().build();
        when(repository.save(chatRoom)).thenReturn(chatRoom);
        when(repository.findById(1L)).thenReturn(Optional.of(chatRoom));

        ChatRoom saved = adapter.save(chatRoom);
        adapter.delete(1L);
        Optional<ChatRoom> found = adapter.find(1L);

        assertThat(saved).isSameAs(chatRoom);
        assertThat(found).contains(chatRoom);
        verify(repository).deleteById(1L);
    }
}
