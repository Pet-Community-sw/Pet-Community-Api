package com.example.petapp.application.service.chatting;

import com.example.petapp.application.out.cache.ChatOnlineCachePort;
import com.example.petapp.domain.chatroom.model.ChatRoom;
import com.example.petapp.domain.chatting.model.ChatMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OfflineUserServiceTest {

    @Mock
    private ChatOnlineCachePort port;

    @InjectMocks
    private OfflineUserService offlineUserService;

    @Test
    void 온라인유저를_제외한_오프라인유저와_안읽은수를_설정한다() {
        ChatRoom chatRoom = org.mockito.Mockito.mock(ChatRoom.class);
        ChatMessage chatMessage = ChatMessage.builder().build();
        when(chatRoom.getId()).thenReturn(10L);
        when(chatRoom.getUsers()).thenReturn(Set.of(1L, 2L, 3L));
        when(port.find(10L)).thenReturn(Set.of("1", "3"));

        offlineUserService.setOfflineUsersAndUnreadCount(chatMessage, chatRoom);

        assertThat(chatMessage.getUsers()).containsExactly(2L);
        assertThat(chatMessage.getUnReadCount()).isEqualTo(1);
    }

    @Test
    void 온라인유저가_없으면_모든유저를_안읽음으로_처리한다() {
        ChatRoom chatRoom = org.mockito.Mockito.mock(ChatRoom.class);
        ChatMessage chatMessage = ChatMessage.builder().build();
        when(chatRoom.getId()).thenReturn(11L);
        when(chatRoom.getUsers()).thenReturn(Set.of(5L, 6L));
        when(port.find(11L)).thenReturn(Set.of());

        offlineUserService.setOfflineUsersAndUnreadCount(chatMessage, chatRoom);

        assertThat(chatMessage.getUsers()).containsExactlyInAnyOrder(5L, 6L);
        assertThat(chatMessage.getUnReadCount()).isEqualTo(2);
    }
}
