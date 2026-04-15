package com.example.petapp.infrastructure.stomp.strategy.subscribe.impl;

import com.example.petapp.application.in.walkrecord.WalkRecordQueryUseCase;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.post.model.DelegateWalkPost;
import com.example.petapp.domain.profile.model.Profile;
import com.example.petapp.domain.walkrecord.model.WalkRecord;
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
class WalkRecordSubscribeStrategyTest {

    @Mock
    private WalkRecordQueryUseCase useCase;

    @InjectMocks
    private WalkRecordSubscribeStrategy strategy;

    @Test
    void 산책구독경로를_핸들링한다() {
        assertThat(strategy.isHandler("/sub/walk/3")).isTrue();
    }

    @Test
    void 산책기록_소유자가_아니면_예외가_발생한다() {
        SubscribeInfo info = SubscribeInfo.builder()
                .destination("/sub/walk/3")
                .principal(() -> "7")
                .build();
        WalkRecord walkRecord = org.mockito.Mockito.mock(WalkRecord.class);
        DelegateWalkPost post = org.mockito.Mockito.mock(DelegateWalkPost.class);
        Profile profile = org.mockito.Mockito.mock(Profile.class);
        Member owner = org.mockito.Mockito.mock(Member.class);

        when(useCase.findOrThrow(3L)).thenReturn(walkRecord);
        when(walkRecord.getDelegateWalkPost()).thenReturn(post);
        when(post.getProfile()).thenReturn(profile);
        when(profile.getMember()).thenReturn(owner);
        when(owner.getId()).thenReturn(8L);

        assertThatThrownBy(() -> strategy.handle(info))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("잘못된 접근입니다.");
    }

    @Test
    void 산책기록_소유자면_정상_처리한다() {
        SubscribeInfo info = SubscribeInfo.builder()
                .destination("/sub/walk/3")
                .principal(() -> "7")
                .build();
        WalkRecord walkRecord = org.mockito.Mockito.mock(WalkRecord.class);
        DelegateWalkPost post = org.mockito.Mockito.mock(DelegateWalkPost.class);
        Profile profile = org.mockito.Mockito.mock(Profile.class);
        Member owner = org.mockito.Mockito.mock(Member.class);

        when(useCase.findOrThrow(3L)).thenReturn(walkRecord);
        when(walkRecord.getDelegateWalkPost()).thenReturn(post);
        when(post.getProfile()).thenReturn(profile);
        when(profile.getMember()).thenReturn(owner);
        when(owner.getId()).thenReturn(7L);

        strategy.handle(info);

        verify(useCase).findOrThrow(3L);
    }
}
