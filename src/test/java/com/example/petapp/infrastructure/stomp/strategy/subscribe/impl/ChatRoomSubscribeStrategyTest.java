package com.example.petapp.infrastructure.stomp.strategy.subscribe.impl;

import com.example.petapp.application.in.chatroom.ChatRoomQueryUseCase;
import com.example.petapp.application.out.cache.ChatOnlineCachePort;
import com.example.petapp.infrastructure.stomp.DestinationCachePort;
import com.example.petapp.infrastructure.stomp.dto.SubscribeInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatRoomSubscribeStrategyTest {

    @Mock
    private ChatRoomQueryUseCase chatRoomQueryUseCase;
    @Mock
    private ChatOnlineCachePort chatOnlineCachePort;
    @Mock
    private DestinationCachePort destinationCachePort;

    @InjectMocks
    private ChatRoomSubscribeStrategy strategy;

    @Test
    void chat구독경로를_핸들링한다() {
        assertThat(strategy.isHandler("/sub/chat/10")).isTrue();
    }

    @Test
    void 채팅방_참여자가_아니면_예외가_발생한다() {
        SubscribeInfo info = SubscribeInfo.builder()
                .subscriptionId("sub-1")
                .destination("/sub/chat/10")
                .principal(() -> "20")
                .build();
        when(chatRoomQueryUseCase.isExist(10L, 20L)).thenReturn(false);

        assertThatThrownBy(() -> strategy.handle(info))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("잘못된 접근입니다.");
    }

    @Test
    void 채팅방_참여자면_온라인캐시와_목적지캐시를_저장한다() {
        SubscribeInfo info = SubscribeInfo.builder()
                .subscriptionId("sub-1")
                .destination("/sub/chat/10")
                .principal(() -> "20")
                .build();
        when(chatRoomQueryUseCase.isExist(10L, 20L)).thenReturn(true);

        strategy.handle(info);

        verify(chatOnlineCachePort).create("10", "20");
        verify(destinationCachePort).create("sub-1", "10");
    }
}
