package com.example.petapp.service;

import com.example.petapp.application.in.location.LocationProcessorUseCase;
import com.example.petapp.application.in.location.dto.request.LocationMessage;
import com.example.petapp.application.in.walkrecord.WalkRecordQueryUseCase;
import com.example.petapp.application.service.location.LocationPipeline;
import com.example.petapp.application.service.location.object.WalkRangeStatus;
import com.example.petapp.domain.walkrecord.model.WalkRecord;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.Executor;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LocationPipelineTest {

    @Mock
    WalkRecordQueryUseCase walkRecordQueryUseCase;

    @Mock
    LocationProcessorUseCase processorUseCase;

    @InjectMocks
    LocationPipeline pipeline;

    Executor directExecutor = Runnable::run;

    @BeforeEach
    void setUp() {
        pipeline = new LocationPipeline(walkRecordQueryUseCase, processorUseCase, directExecutor);
    }

    @Test
    void 동일한_walkRecordId로_여러_번_send해도_초기화_한번_성공() {
        WalkRecord walkRecord = mock(WalkRecord.class);
        when(walkRecordQueryUseCase.findOrThrow(1L)).thenReturn(walkRecord);

        LocationMessage message1 = mock(LocationMessage.class);
        when(message1.getWalkRecordId()).thenReturn(1L);

        LocationMessage message2 = mock(LocationMessage.class);
        when(message2.getWalkRecordId()).thenReturn(1L);

        pipeline.send(message1, "1");
        pipeline.send(message2, "1");

        verify(walkRecordQueryUseCase, times(1)).findOrThrow(1L);
        verify(walkRecord, times(1)).validateMember(1L);
        verify(walkRecord, times(1)).validateStart();
    }

    @Test
    void 다른_walkRecordId로_각각_send하면_각각_초기화_성공() {
        WalkRecord walkRecord1 = mock(WalkRecord.class);
        WalkRecord walkRecord2 = mock(WalkRecord.class);

        when(walkRecordQueryUseCase.findOrThrow(1L)).thenReturn(walkRecord1);
        when(walkRecordQueryUseCase.findOrThrow(2L)).thenReturn(walkRecord2);

        LocationMessage message1 = mock(LocationMessage.class);
        when(message1.getWalkRecordId()).thenReturn(1L);

        LocationMessage message2 = mock(LocationMessage.class);
        when(message2.getWalkRecordId()).thenReturn(2L);

        pipeline.send(message1, "1");
        pipeline.send(message2, "2");

        verify(walkRecordQueryUseCase, times(1)).findOrThrow(1L);
        verify(walkRecordQueryUseCase, times(1)).findOrThrow(2L);
        verify(walkRecord1, times(1)).validateMember(1L);
        verify(walkRecord1, times(1)).validateStart();
        verify(walkRecord2, times(1)).validateMember(2L);
        verify(walkRecord2, times(1)).validateStart();
    }

    @Test
    void 초기화_실패하면_initMap에서_제거되어_다음요청때_다시_초기화_성공() {
        WalkRecord walkRecord = mock(WalkRecord.class);
        //첫 번째 요청은 에러 두 번째 요청은 정상
        when(walkRecordQueryUseCase.findOrThrow(1L))
                .thenThrow(new RuntimeException("test error"))
                .thenReturn(walkRecord);

        LocationMessage message = mock(LocationMessage.class);
        when(message.getWalkRecordId()).thenReturn(1L);

        //첫 번째 호출 - 예외 발생
        Assertions.assertThatThrownBy(() -> pipeline.send(message, "1"))
                .hasRootCauseMessage("test error");// 루트 원인 예외 메시지 확인
        //두 번째 호출 - 정상 처리
        pipeline.send(message, "1");

        //initMap에서 제거되어 두 번 호출됨
        verify(walkRecordQueryUseCase, times(2)).findOrThrow(1L);
        verify(walkRecord, times(1)).validateMember(1L);
        verify(walkRecord, times(1)).validateStart();
    }

    @Test
    void 여려번_호출해도_throttle_적용되어_2초에_한건_처리_성공() throws InterruptedException {
        WalkRecord walkRecord = mock(WalkRecord.class);
        when(walkRecordQueryUseCase.findOrThrow(1L)).thenReturn(walkRecord);

        LocationMessage message = mock(LocationMessage.class);
        when(message.getWalkRecordId()).thenReturn(1L);

        when(processorUseCase.isEnoughMove(message)).thenReturn(true);

        WalkRangeStatus walkRangeStatus = mock(WalkRangeStatus.class);
        when(processorUseCase.checkRange(walkRecord, message)).thenReturn(walkRangeStatus);

        pipeline.send(message, "1");
        Thread.sleep(3000);
        pipeline.send(message, "1");

        verify(processorUseCase, times(2)).isEnoughMove(message);
    }

}
