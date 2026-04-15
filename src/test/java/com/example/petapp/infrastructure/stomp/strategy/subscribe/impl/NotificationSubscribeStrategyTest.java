package com.example.petapp.infrastructure.stomp.strategy.subscribe.impl;

import com.example.petapp.application.in.member.MemberQueryUseCase;
import com.example.petapp.infrastructure.stomp.dto.SubscribeInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationSubscribeStrategyTest {

    @Mock
    private MemberQueryUseCase useCase;

    @InjectMocks
    private NotificationSubscribeStrategy strategy;

    @Test
    void 알림구독경로를_핸들링한다() {
        assertThat(strategy.isHandler("/sub/notification/5")).isTrue();
    }

    @Test
    void 알림구독시_member를_검증한다() {
        SubscribeInfo info = SubscribeInfo.builder()
                .destination("/sub/notification/5")
                .principal(() -> "1")
                .build();

        strategy.handle(info);

        verify(useCase).findOrThrow(5L);
    }
}
