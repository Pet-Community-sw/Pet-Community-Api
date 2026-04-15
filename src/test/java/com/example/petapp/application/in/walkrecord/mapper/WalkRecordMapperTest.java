package com.example.petapp.application.in.walkrecord.mapper;

import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.post.model.DelegateWalkPost;
import com.example.petapp.domain.walkrecord.model.WalkRecord;
import com.example.petapp.domain.walkrecord.model.WalkStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class WalkRecordMapperTest {

    @Test
    void 산책기록_생성시_필수값을_초기화한다() {
        DelegateWalkPost delegateWalkPost = mock(DelegateWalkPost.class);
        Member member = mock(Member.class);

        WalkRecord walkRecord = WalkRecordMapper.toEntity(delegateWalkPost, member);

        assertThat(walkRecord.getWalkStatus()).isEqualTo(WalkStatus.READY);
        assertThat(walkRecord.getStartTime()).isNotNull();
        assertThat(walkRecord.getFinishTime()).isNotNull();
        assertThat(walkRecord.getWalkDistance()).isEqualTo(0D);
        assertThat(walkRecord.getDelegateWalkPost()).isSameAs(delegateWalkPost);
        assertThat(walkRecord.getMember()).isSameAs(member);
    }
}
