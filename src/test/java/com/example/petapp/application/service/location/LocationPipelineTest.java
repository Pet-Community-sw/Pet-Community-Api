package com.example.petapp.application.service.location;

import com.example.petapp.application.in.location.LocationProcessorUseCase;
import com.example.petapp.application.in.location.dto.request.LocationMessage;
import com.example.petapp.application.in.walkrecord.WalkRecordQueryUseCase;
import com.example.petapp.application.service.location.object.WalkRangeStatus;
import com.example.petapp.domain.walkrecord.model.WalkRecord;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.schedulers.TestScheduler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocationPipelineTest {

    @Mock
    private WalkRecordQueryUseCase walkRecordQueryUseCase;

    @Mock
    private LocationProcessorUseCase processorUseCase;

    private final Executor directExecutor = Runnable::run;

    private LocationPipeline pipeline;
    private TestScheduler testScheduler;

    @BeforeEach
    void 테스트를_준비한다() {
        RxJavaPlugins.reset();
        testScheduler = new TestScheduler();
        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> testScheduler);
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        pipeline = new LocationPipeline(walkRecordQueryUseCase, processorUseCase, directExecutor);
    }

    @AfterEach
    void 테스트환경을_정리한다() {
        RxJavaPlugins.reset();
    }

    @Test
    void 같은_산책기록_ID로_전송하면_파이프라인은_한번만_초기화된다() {
        WalkRecord walkRecord = 산책기록을_생성한다(1L);
        when(walkRecordQueryUseCase.findAndValidate(1L, 1L)).thenReturn(walkRecord);

        전송하고_처리한다(위치메시지를_생성한다(1L, 37.1, 127.1), "1");
        전송하고_처리한다(위치메시지를_생성한다(1L, 37.2, 127.2), "1");

        verify(walkRecordQueryUseCase, times(1)).findAndValidate(1L, 1L);
    }

    @Test
    void 다른_산책기록_ID로_전송하면_각각_독립적으로_초기화된다() {
        WalkRecord walkRecord1 = 산책기록을_생성한다(1L);
        WalkRecord walkRecord2 = 산책기록을_생성한다(2L);
        when(walkRecordQueryUseCase.findAndValidate(1L, 1L)).thenReturn(walkRecord1);
        when(walkRecordQueryUseCase.findAndValidate(2L, 2L)).thenReturn(walkRecord2);

        전송하고_처리한다(위치메시지를_생성한다(1L, 37.1, 127.1), "1");
        전송하고_처리한다(위치메시지를_생성한다(2L, 37.2, 127.2), "2");

        verify(walkRecordQueryUseCase).findAndValidate(1L, 1L);
        verify(walkRecordQueryUseCase).findAndValidate(2L, 2L);
    }

    @Test
    void 초기화에_실패하면_다음_전송에서_다시_초기화한다() {
        WalkRecord walkRecord = 산책기록을_생성한다(1L);
        when(walkRecordQueryUseCase.findAndValidate(1L, 1L))
                .thenThrow(new RuntimeException("init failure"))
                .thenReturn(walkRecord);

        전송하고_처리한다(위치메시지를_생성한다(1L, 37.1, 127.1), "1");
        전송하고_처리한다(위치메시지를_생성한다(1L, 37.2, 127.2), "1");

        verify(walkRecordQueryUseCase, times(2)).findAndValidate(1L, 1L);
    }

    @Test
    void 다른_회원_ID는_기존_파이프라인에_메시지를_보낼_수_없다() {
        WalkRecord walkRecord = 산책기록을_생성한다(1L);
        when(walkRecordQueryUseCase.findAndValidate(1L, 1L)).thenReturn(walkRecord);
        when(processorUseCase.isEnoughMove(any(LocationMessage.class))).thenReturn(true);
        when(processorUseCase.checkRange(eq(walkRecord), any(LocationMessage.class)))
                .thenReturn(new WalkRangeStatus(10, false));

        전송하고_처리한다(위치메시지를_생성한다(1L, 37.1, 127.1), "1");
        시간을_앞당긴다(2, TimeUnit.SECONDS);
        전송하고_처리한다(위치메시지를_생성한다(1L, 37.2, 127.2), "2");

        verify(walkRecordQueryUseCase, times(1)).findAndValidate(1L, 1L);
        verify(walkRecordQueryUseCase, never()).findAndValidate(1L, 2L);
        verify(processorUseCase, times(1)).saveAndSend(any(LocationMessage.class));
    }

    @Test
    void 스로틀_구간에서는_첫_이벤트만_처리한다() {
        WalkRecord walkRecord = 산책기록을_생성한다(1L);
        when(walkRecordQueryUseCase.findAndValidate(1L, 1L)).thenReturn(walkRecord);
        when(processorUseCase.isEnoughMove(any(LocationMessage.class))).thenReturn(true);
        when(processorUseCase.checkRange(eq(walkRecord), any(LocationMessage.class)))
                .thenReturn(new WalkRangeStatus(10, false));

        전송하고_처리한다(위치메시지를_생성한다(1L, 37.1, 127.1), "1");
        전송하고_처리한다(위치메시지를_생성한다(1L, 37.1001, 127.1001), "1");

        verify(processorUseCase, times(1)).saveAndSend(any(LocationMessage.class));

        시간을_앞당긴다(2, TimeUnit.SECONDS);
        전송하고_처리한다(위치메시지를_생성한다(1L, 37.2, 127.2), "1");

        verify(processorUseCase, times(2)).saveAndSend(any(LocationMessage.class));
    }

    @Test
    void 이동량이_부족하면_이후_파이프라인_처리를_중단한다() {
        WalkRecord walkRecord = 산책기록을_생성한다(1L);
        when(walkRecordQueryUseCase.findAndValidate(1L, 1L)).thenReturn(walkRecord);
        when(processorUseCase.isEnoughMove(any(LocationMessage.class))).thenReturn(false);

        전송하고_처리한다(위치메시지를_생성한다(1L, 37.1, 127.1), "1");

        verify(processorUseCase).isEnoughMove(any(LocationMessage.class));
        verify(processorUseCase, never()).saveAndSend(any(LocationMessage.class));
        verify(processorUseCase, never()).checkRange(any(WalkRecord.class), any(LocationMessage.class));
        verify(processorUseCase, never()).sendNotification(any(WalkRecord.class), any(WalkRangeStatus.class));
    }

    @Test
    void 같은_범위_상태가_반복되면_알림은_한번만_보낸다() {
        WalkRecord walkRecord = 산책기록을_생성한다(1L);
        LocationMessage first = 위치메시지를_생성한다(1L, 37.1, 127.1);
        LocationMessage second = 위치메시지를_생성한다(1L, 37.2, 127.2);

        when(walkRecordQueryUseCase.findAndValidate(1L, 1L)).thenReturn(walkRecord);
        when(processorUseCase.isEnoughMove(any(LocationMessage.class))).thenReturn(true);
        when(processorUseCase.checkRange(walkRecord, first)).thenReturn(new WalkRangeStatus(10, false));
        when(processorUseCase.checkRange(walkRecord, second)).thenReturn(new WalkRangeStatus(25, false));

        전송하고_처리한다(first, "1");
        시간을_앞당긴다(2, TimeUnit.SECONDS);
        전송하고_처리한다(second, "1");

        verify(processorUseCase, times(2)).saveAndSend(any(LocationMessage.class));
        verify(processorUseCase, times(1)).sendNotification(eq(walkRecord), any(WalkRangeStatus.class));
    }

    @Test
    void 범위_상태가_변경되면_알림을_다시_보낸다() {
        WalkRecord walkRecord = 산책기록을_생성한다(1L);
        LocationMessage first = 위치메시지를_생성한다(1L, 37.1, 127.1);
        LocationMessage second = 위치메시지를_생성한다(1L, 37.2, 127.2);

        when(walkRecordQueryUseCase.findAndValidate(1L, 1L)).thenReturn(walkRecord);
        when(processorUseCase.isEnoughMove(any(LocationMessage.class))).thenReturn(true);
        when(processorUseCase.checkRange(walkRecord, first)).thenReturn(new WalkRangeStatus(10, false));
        when(processorUseCase.checkRange(walkRecord, second)).thenReturn(new WalkRangeStatus(30, true));

        전송하고_처리한다(first, "1");
        시간을_앞당긴다(2, TimeUnit.SECONDS);
        전송하고_처리한다(second, "1");

        verify(processorUseCase).sendNotification(walkRecord, new WalkRangeStatus(10, false));
        verify(processorUseCase).sendNotification(walkRecord, new WalkRangeStatus(30, true));
    }

    @Test
    void 저장과_전송이_실패해도_다음_이벤트는_계속_처리한다() {
        WalkRecord walkRecord = 산책기록을_생성한다(1L);
        LocationMessage first = 위치메시지를_생성한다(1L, 37.1, 127.1);
        LocationMessage second = 위치메시지를_생성한다(1L, 37.2, 127.2);

        when(walkRecordQueryUseCase.findAndValidate(1L, 1L)).thenReturn(walkRecord);
        when(processorUseCase.isEnoughMove(any(LocationMessage.class))).thenReturn(true);
        doThrow(new RuntimeException("save failure")).when(processorUseCase).saveAndSend(first);
        when(processorUseCase.checkRange(walkRecord, second)).thenReturn(new WalkRangeStatus(40, true));

        전송하고_처리한다(first, "1");
        시간을_앞당긴다(2, TimeUnit.SECONDS);
        전송하고_처리한다(second, "1");

        verify(processorUseCase).saveAndSend(first);
        verify(processorUseCase).saveAndSend(second);
        verify(processorUseCase, never()).checkRange(walkRecord, first);
        verify(processorUseCase).checkRange(walkRecord, second);
        verify(processorUseCase).sendNotification(walkRecord, new WalkRangeStatus(40, true));
    }

    @Test
    void 시간초과가_발생하면_정리하고_다음_전송에서_재초기화한다() {
        WalkRecord walkRecord = 산책기록을_생성한다(1L);
        when(walkRecordQueryUseCase.findAndValidate(1L, 1L)).thenReturn(walkRecord);

        전송하고_처리한다(위치메시지를_생성한다(1L, 37.1, 127.1), "1");
        시간을_앞당긴다(10, TimeUnit.MINUTES);
        시간을_앞당긴다(1, TimeUnit.SECONDS);

        verify(processorUseCase).clean(1L);

        전송하고_처리한다(위치메시지를_생성한다(1L, 37.2, 127.2), "1");

        verify(walkRecordQueryUseCase, times(2)).findAndValidate(1L, 1L);
    }

    @Test
    void 수동_정리후에는_파이프라인을_다시_초기화할_수_있다() {
        WalkRecord walkRecord = 산책기록을_생성한다(1L);
        when(walkRecordQueryUseCase.findAndValidate(1L, 1L)).thenReturn(walkRecord);

        전송하고_처리한다(위치메시지를_생성한다(1L, 37.1, 127.1), "1");
        pipeline.clean(1L);
        전송하고_처리한다(위치메시지를_생성한다(1L, 37.2, 127.2), "1");

        verify(processorUseCase).clean(1L);
        verify(walkRecordQueryUseCase, times(2)).findAndValidate(1L, 1L);
    }

    private void 전송하고_처리한다(LocationMessage message, String memberId) {
        pipeline.send(message, memberId);
        testScheduler.triggerActions();
    }

    private void 시간을_앞당긴다(long time, TimeUnit unit) {
        testScheduler.advanceTimeBy(time, unit);
        testScheduler.triggerActions();
    }

    private WalkRecord 산책기록을_생성한다(Long id) {
        WalkRecord walkRecord = org.mockito.Mockito.mock(WalkRecord.class);
        when(walkRecord.getId()).thenReturn(id);
        return walkRecord;
    }

    private LocationMessage 위치메시지를_생성한다(Long walkRecordId, double latitude, double longitude) {
        return LocationMessage.builder()
                .walkRecordId(walkRecordId)
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }
}
