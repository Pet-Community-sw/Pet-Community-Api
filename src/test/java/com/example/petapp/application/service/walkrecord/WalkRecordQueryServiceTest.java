package com.example.petapp.application.service.walkrecord;

import com.example.petapp.domain.walkrecord.WalkRecordRepository;
import com.example.petapp.domain.walkrecord.model.WalkRecord;
import com.example.petapp.interfaces.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalkRecordQueryServiceTest {

    @Mock
    private WalkRecordRepository repository;

    @InjectMocks
    private WalkRecordQueryService walkRecordQueryService;

    @Test
    void 산책기록이_존재하면_findOrThrow_조회에_성공한다() {
        WalkRecord walkRecord = org.mockito.Mockito.mock(WalkRecord.class);
        when(repository.find(1L)).thenReturn(Optional.of(walkRecord));

        WalkRecord result = walkRecordQueryService.findOrThrow(1L);

        assertThat(result).isSameAs(walkRecord);
        verify(repository).find(1L);
    }

    @Test
    void 산책기록이_없으면_findOrThrow에서_예외가_발생한다() {
        when(repository.find(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> walkRecordQueryService.findOrThrow(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("해당 산책기록은 없습니다.");
    }

    @Test
    void findAndValidate는_회원검증과_시작상태검증을_수행한다() {
        WalkRecord walkRecord = org.mockito.Mockito.mock(WalkRecord.class);
        when(repository.find(1L)).thenReturn(Optional.of(walkRecord));

        WalkRecord result = walkRecordQueryService.findAndValidate(1L, 10L);

        assertThat(result).isSameAs(walkRecord);
        InOrder inOrder = inOrder(walkRecord);
        inOrder.verify(walkRecord).validateMember(10L);
        inOrder.verify(walkRecord).validateStart();
    }
}
