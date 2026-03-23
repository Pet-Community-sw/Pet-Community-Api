package com.example.petapp.service;

import com.example.petapp.application.in.location.LocationProcessorUseCase;
import com.example.petapp.application.in.location.dto.request.LocationMessage;
import com.example.petapp.application.in.walkrecord.WalkRecordQueryUseCase;
import com.example.petapp.application.service.location.LocationPipeline;
import com.example.petapp.application.service.location.object.WalkRangeStatus;
import com.example.petapp.domain.walkrecord.model.WalkRecord;
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

        when(walkRecordQueryUseCase.findAndValidate(1L, 1L)).thenReturn(walkRecord);

        LocationMessage message1 = mock(LocationMessage.class);
        when(message1.getWalkRecordId()).thenReturn(1L);

        LocationMessage message2 = mock(LocationMessage.class);
        when(message2.getWalkRecordId()).thenReturn(1L);

        //두 번의 send 호출 - 초기화는 한 번만 수행되어야 함
        pipeline.send(message1, "1");
        pipeline.send(message2, "1");

        verify(walkRecordQueryUseCase, times(1)).findAndValidate(1L, 1L);
    }

    @Test
    void 다른_walkRecordId로_각각_send하면_각각_초기화_성공() {
        WalkRecord walkRecord1 = mock(WalkRecord.class);
        WalkRecord walkRecord2 = mock(WalkRecord.class);

        when(walkRecordQueryUseCase.findAndValidate(1L, 1L)).thenReturn(walkRecord1);
        when(walkRecordQueryUseCase.findAndValidate(2L, 2L)).thenReturn(walkRecord2);

        LocationMessage message1 = mock(LocationMessage.class);
        when(message1.getWalkRecordId()).thenReturn(1L);

        LocationMessage message2 = mock(LocationMessage.class);
        when(message2.getWalkRecordId()).thenReturn(2L);

        pipeline.send(message1, "1");
        pipeline.send(message2, "2");

        verify(walkRecordQueryUseCase, times(1)).findAndValidate(1L, 1L);
        verify(walkRecordQueryUseCase, times(1)).findAndValidate(2L, 2L);
    }

    @Test
    void 초기화_실패하면_initMap에서_제거되어_다음요청때_다시_초기화_성공() {
        WalkRecord walkRecord = mock(WalkRecord.class);
        when(walkRecord.getId()).thenReturn(1L);

        // 1회차 호출 시엔 에러, 2회차엔 정상 객체 반환하도록 설정
        when(walkRecordQueryUseCase.findAndValidate(1L, 1L))
                .thenThrow(new RuntimeException("test error"))
                .thenReturn(walkRecord);

        LocationMessage message = mock(LocationMessage.class);
        when(message.getWalkRecordId()).thenReturn(1L);

        //첫 번째 호출
        pipeline.send(message, "1");

        //두 번째 호출
        pipeline.send(message, "1");

        //findAndValidate 2번 호출
        verify(walkRecordQueryUseCase, times(2)).findAndValidate(1L, 1L);
    }

    @Test
    void 여려번_호출해도_throttle_적용되어_2초에_한건_처리_성공() throws InterruptedException {
        WalkRecord walkRecord = mock(WalkRecord.class);
        when(walkRecordQueryUseCase.findAndValidate(1L, 1L)).thenReturn(walkRecord);

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
