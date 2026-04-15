package com.example.petapp.infrastructure.stomp.strategy.subscribe.impl;

import com.example.petapp.application.in.profile.ProfileQueryUseCase;
import com.example.petapp.infrastructure.stomp.dto.SubscribeInfo;
import com.example.petapp.interfaces.exception.ForbiddenException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RoomListSubscribeStrategyTest {

    @Mock
    private ProfileQueryUseCase useCase;

    @InjectMocks
    private RoomListSubscribeStrategy strategy;

    @Test
    void list구독경로를_핸들링한다() {
        assertThat(strategy.isHandler("/sub/list/10")).isTrue();
    }

    @Test
    void 본인_userId로_구독하면_프로필조회가_수행된다() {
        SubscribeInfo info = SubscribeInfo.builder()
                .destination("/sub/list/10")
                .principal(() -> "10")
                .build();

        strategy.handle(info);

        verify(useCase).findOrThrow(10L);
    }

    @Test
    void 다른_userId로_구독하면_예외가_발생한다() {
        SubscribeInfo info = SubscribeInfo.builder()
                .destination("/sub/list/10")
                .principal(() -> "11")
                .build();

        assertThatThrownBy(() -> strategy.handle(info))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("[STOMP] userId가 다릅니다.");
    }
}
