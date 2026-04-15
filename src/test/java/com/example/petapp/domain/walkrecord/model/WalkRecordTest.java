package com.example.petapp.domain.walkrecord.model;

import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.post.model.DelegateWalkPost;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class WalkRecordTest {

    @Test
    void 산책_시작상태_변경시_startTime만_갱신한다() {
        LocalDateTime oldStartTime = LocalDateTime.of(2024, 1, 1, 1, 0);
        LocalDateTime oldFinishTime = LocalDateTime.of(2024, 1, 1, 2, 0);
        WalkRecord walkRecord = createWalkRecord(oldStartTime, oldFinishTime);

        walkRecord.updateWalkStatus(WalkStatus.START);

        assertThat(walkRecord.getWalkStatus()).isEqualTo(WalkStatus.START);
        assertThat(walkRecord.getStartTime()).isNotEqualTo(oldStartTime);
        assertThat(walkRecord.getFinishTime()).isEqualTo(oldFinishTime);
    }

    @Test
    void 산책_종료상태_변경시_finishTime만_갱신한다() {
        LocalDateTime oldStartTime = LocalDateTime.of(2024, 1, 1, 1, 0);
        LocalDateTime oldFinishTime = LocalDateTime.of(2024, 1, 1, 2, 0);
        WalkRecord walkRecord = createWalkRecord(oldStartTime, oldFinishTime);

        walkRecord.updateWalkStatus(WalkStatus.FINISH);

        assertThat(walkRecord.getWalkStatus()).isEqualTo(WalkStatus.FINISH);
        assertThat(walkRecord.getStartTime()).isEqualTo(oldStartTime);
        assertThat(walkRecord.getFinishTime()).isNotEqualTo(oldFinishTime);
    }

    private WalkRecord createWalkRecord(LocalDateTime startTime, LocalDateTime finishTime) {
        return WalkRecord.builder()
                .startTime(startTime)
                .finishTime(finishTime)
                .walkDistance(0D)
                .walkStatus(WalkStatus.READY)
                .delegateWalkPost(mock(DelegateWalkPost.class))
                .member(mock(Member.class))
                .build();
    }
}
