package com.example.petapp.infrastructure.database.adapter;

import com.example.petapp.domain.chatting.model.ChatMessage;
import com.example.petapp.infrastructure.database.mongo.MongoChatMessageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatMessageRepositoryAdapterTest {

    @Mock
    private MongoChatMessageRepository repository;

    @InjectMocks
    private ChatMessageRepositoryAdapter adapter;

    @Test
    void save는_mongo저장소에_위임한다() {
        ChatMessage chatMessage = ChatMessage.builder().chatRoomId(1L).build();

        adapter.save(chatMessage);

        verify(repository).save(chatMessage);
    }

    @Test
    void findAll은_채팅방기준_seq오름차순_조회에_위임한다() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<ChatMessage> page = new PageImpl<>(List.of(ChatMessage.builder().build()));
        when(repository.findAllByChatRoomIdOrderBySeqAsc(1L, pageable)).thenReturn(page);

        Page<ChatMessage> result = adapter.findAll(1L, pageable);

        assertThat(result).isSameAs(page);
    }

    @Test
    void findAllBySeq는_seq이후_조회에_위임한다() {
        List<ChatMessage> list = List.of(ChatMessage.builder().seq(3L).build());
        when(repository.findAllByChatRoomIdAndSeqGreaterThanOrderBySeqAsc(1L, 2L)).thenReturn(list);

        List<ChatMessage> result = adapter.findAllBySeq(1L, 2L);

        assertThat(result).isSameAs(list);
    }

    @Test
    void findCurrent는_최신메시지_조회에_위임한다() {
        ChatMessage current = ChatMessage.builder().seq(10L).build();
        when(repository.findFirstByChatRoomIdOrderBySeqDesc(1L)).thenReturn(Optional.of(current));

        Optional<ChatMessage> result = adapter.findCurrent(1L);

        assertThat(result).contains(current);
    }

    @Test
    void delete는_채팅방기준_삭제에_위임한다() {
        adapter.delete(1L);
        verify(repository).deleteByChatRoomId(1L);
    }
}
