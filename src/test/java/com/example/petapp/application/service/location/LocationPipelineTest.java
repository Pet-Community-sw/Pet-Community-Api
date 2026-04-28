package com.example.petapp.application.service.location;

import com.example.petapp.application.in.location.LocationProcessorUseCase;
import com.example.petapp.application.in.location.dto.request.LocationMessage;
import com.example.petapp.application.in.walkrecord.WalkRecordUseCase;
import com.example.petapp.application.service.location.object.WalkRangeStatus;
import com.example.petapp.domain.walkrecord.model.WalkRecord;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.schedulers.TestScheduler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocationPipelineTest {

    @Mock
    private WalkRecordUseCase walkRecordUseCase;

    @Mock
    private ObjectProvider<WalkRecordUseCase> walkRecordUseCaseProvider;

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
        when(walkRecordUseCaseProvider.getObject()).thenReturn(walkRecordUseCase);
        pipeline = new LocationPipeline(walkRecordUseCaseProvider, processorUseCase, directExecutor);
    }

    @AfterEach
    void 테스트환경을_정리한다() {
        RxJavaPlugins.reset();
    }

    @Test
    void 같은_산책기록_ID로_전송하면_파이프라인은_한번만_초기화된다() {
        WalkRecord walkRecord = 산책기록을_생성한다(1L);
        when(walkRecordUseCase.findAndValidate(1L, 1L)).thenReturn(walkRecord);

        전송하고_처리한다(위치메시지를_생성한다(1L, 37.1, 127.1), "1");
        전송하고_처리한다(위치메시지를_생성한다(1L, 37.2, 127.2), "1");

        verify(walkRecordUseCase, times(1)).findAndValidate(1L, 1L);
    }

    @Test
    void 다른_산책기록_ID로_전송하면_각각_독립적으로_초기화된다() {
        WalkRecord walkRecord1 = 산책기록을_생성한다(1L);
        WalkRecord walkRecord2 = 산책기록을_생성한다(2L);
        when(walkRecordUseCase.findAndValidate(1L, 1L)).thenReturn(walkRecord1);
        when(walkRecordUseCase.findAndValidate(2L, 2L)).thenReturn(walkRecord2);

        전송하고_처리한다(위치메시지를_생성한다(1L, 37.1, 127.1), "1");
        전송하고_처리한다(위치메시지를_생성한다(2L, 37.2, 127.2), "2");

        verify(walkRecordUseCase).findAndValidate(1L, 1L);
        verify(walkRecordUseCase).findAndValidate(2L, 2L);
    }

    @Test
    void 초기화에_실패하면_다음_전송에서_다시_초기화한다() {
        WalkRecord walkRecord = 산책기록을_생성한다(1L);
        when(walkRecordUseCase.findAndValidate(1L, 1L))
                .thenThrow(new RuntimeException("init failure"))
                .thenReturn(walkRecord);

        전송하고_처리한다(위치메시지를_생성한다(1L, 37.1, 127.1), "1");
        전송하고_처리한다(위치메시지를_생성한다(1L, 37.2, 127.2), "1");

        verify(walkRecordUseCase, times(2)).findAndValidate(1L, 1L);
    }

    @Test
    void 다른_회원_ID는_기존_파이프라인에_메시지를_보낼_수_없다() {
        WalkRecord walkRecord = 산책기록을_생성한다(1L);
        when(walkRecordUseCase.findAndValidate(1L, 1L)).thenReturn(walkRecord);
        when(processorUseCase.isEnoughMove(any(LocationMessage.class))).thenReturn(true);
        when(processorUseCase.checkRange(eq(walkRecord), any(LocationMessage.class)))
                .thenReturn(new WalkRangeStatus(10, false));

        전송하고_처리한다(위치메시지를_생성한다(1L, 37.1, 127.1), "1");
        시간을_앞당긴다(2, TimeUnit.SECONDS);
        전송하고_처리한다(위치메시지를_생성한다(1L, 37.2, 127.2), "2");

        verify(walkRecordUseCase, times(1)).findAndValidate(1L, 1L);
        verify(walkRecordUseCase, never()).findAndValidate(1L, 2L);
        verify(processorUseCase, times(1)).saveAndSend(any(LocationMessage.class));
    }

    @Test
    void 스로틀_구간에서는_첫_이벤트만_처리한다() {
        WalkRecord walkRecord = 산책기록을_생성한다(1L);
        when(walkRecordUseCase.findAndValidate(1L, 1L)).thenReturn(walkRecord);
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
        when(walkRecordUseCase.findAndValidate(1L, 1L)).thenReturn(walkRecord);
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

        when(walkRecordUseCase.findAndValidate(1L, 1L)).thenReturn(walkRecord);
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

        when(walkRecordUseCase.findAndValidate(1L, 1L)).thenReturn(walkRecord);
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

        when(walkRecordUseCase.findAndValidate(1L, 1L)).thenReturn(walkRecord);
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
        when(walkRecordUseCase.findAndValidate(1L, 1L)).thenReturn(walkRecord);

        전송하고_처리한다(위치메시지를_생성한다(1L, 37.1, 127.1), "1");
        시간을_앞당긴다(10, TimeUnit.MINUTES);
        시간을_앞당긴다(1, TimeUnit.SECONDS);

        verify(processorUseCase).clean(1L);

        전송하고_처리한다(위치메시지를_생성한다(1L, 37.2, 127.2), "1");

        verify(walkRecordUseCase, times(2)).findAndValidate(1L, 1L);
    }

    @Test
    void 수동_정리후에는_파이프라인을_다시_초기화할_수_있다() {
        WalkRecord walkRecord = 산책기록을_생성한다(1L);
        when(walkRecordUseCase.findAndValidate(1L, 1L)).thenReturn(walkRecord);

        전송하고_처리한다(위치메시지를_생성한다(1L, 37.1, 127.1), "1");
        pipeline.clean(1L);
        전송하고_처리한다(위치메시지를_생성한다(1L, 37.2, 127.2), "1");

        verify(processorUseCase).clean(1L);
        verify(walkRecordUseCase, times(2)).findAndValidate(1L, 1L);
    }

    @Test
    void 첫_위치_이벤트는_파이프라인_스레드에서_필터링되고_저장부터는_IO_스레드에서_처리된다() throws InterruptedException {
        ExecutorService pipelineExecutor = Executors.newSingleThreadExecutor(runnable -> new Thread(runnable, "location-pipeline-thread"));
        ExecutorService ioExecutor = Executors.newSingleThreadExecutor(runnable -> new Thread(runnable, "location-io-thread"));
        AtomicReference<String> 이동검증_스레드 = new AtomicReference<>();
        AtomicReference<String> 저장_스레드 = new AtomicReference<>();
        AtomicReference<String> 범위계산_스레드 = new AtomicReference<>();
        AtomicReference<String> 알림_스레드 = new AtomicReference<>();
        CountDownLatch 완료대기 = new CountDownLatch(1);

        try {
            테스트용_스케줄러를_재구성한다(pipelineExecutor, ioExecutor);

            WalkRecord walkRecord = 산책기록을_생성한다(1L);
            when(walkRecordUseCase.findAndValidate(1L, 1L)).thenReturn(walkRecord);
            when(processorUseCase.isEnoughMove(any(LocationMessage.class))).thenAnswer(invocation -> {
                이동검증_스레드.set(Thread.currentThread().getName());
                return true;
            });
            when(processorUseCase.checkRange(eq(walkRecord), any(LocationMessage.class))).thenAnswer(invocation -> {
                범위계산_스레드.set(Thread.currentThread().getName());
                return new WalkRangeStatus(10, false);
            });
            org.mockito.Mockito.doAnswer(invocation -> {
                저장_스레드.set(Thread.currentThread().getName());
                return null;
            }).when(processorUseCase).saveAndSend(any(LocationMessage.class));
            org.mockito.Mockito.doAnswer(invocation -> {
                알림_스레드.set(Thread.currentThread().getName());
                완료대기.countDown();
                return null;
            }).when(processorUseCase).sendNotification(eq(walkRecord), any(WalkRangeStatus.class));

            이름있는_스레드에서_실행한다("stomp-thread", () ->
                    pipeline.send(위치메시지를_생성한다(1L, 37.1, 127.1), "1"));

            assertTrue(완료대기.await(3, TimeUnit.SECONDS));
            assertEquals("location-pipeline-thread", 이동검증_스레드.get());
            assertEquals("location-io-thread", 저장_스레드.get());
            assertEquals("location-io-thread", 범위계산_스레드.get());
            assertEquals("location-io-thread", 알림_스레드.get());
        } finally {
            pipelineExecutor.shutdownNow();
            ioExecutor.shutdownNow();
        }
    }

    @Test
    void 초기화된_이후_위치_이벤트도_파이프라인_스레드에서_필터링되고_저장부터는_IO_스레드에서_처리된다() throws InterruptedException {
        ExecutorService pipelineExecutor = Executors.newSingleThreadExecutor(runnable -> new Thread(runnable, "location-pipeline-thread"));
        ExecutorService ioExecutor = Executors.newSingleThreadExecutor(runnable -> new Thread(runnable, "location-io-thread"));
        AtomicReference<String> 이동검증_스레드 = new AtomicReference<>();
        AtomicReference<String> 저장_스레드 = new AtomicReference<>();
        AtomicReference<String> 범위계산_스레드 = new AtomicReference<>();
        AtomicReference<String> 알림_스레드 = new AtomicReference<>();
        AtomicInteger 범위계산_호출횟수 = new AtomicInteger();
        CountDownLatch 첫이벤트_완료대기 = new CountDownLatch(1);
        CountDownLatch 두번째이벤트_완료대기 = new CountDownLatch(1);

        try {
            테스트용_스케줄러를_재구성한다(pipelineExecutor, ioExecutor);

            WalkRecord walkRecord = 산책기록을_생성한다(1L);
            when(walkRecordUseCase.findAndValidate(1L, 1L)).thenReturn(walkRecord);
            when(processorUseCase.isEnoughMove(any(LocationMessage.class))).thenAnswer(invocation -> {
                이동검증_스레드.set(Thread.currentThread().getName());
                return true;
            });
            when(processorUseCase.checkRange(eq(walkRecord), any(LocationMessage.class))).thenAnswer(invocation -> {
                범위계산_스레드.set(Thread.currentThread().getName());
                if (범위계산_호출횟수.incrementAndGet() == 1) {
                    return new WalkRangeStatus(10, false);
                }
                return new WalkRangeStatus(20, true);
            });
            org.mockito.Mockito.doAnswer(invocation -> {
                저장_스레드.set(Thread.currentThread().getName());
                return null;
            }).when(processorUseCase).saveAndSend(any(LocationMessage.class));
            org.mockito.Mockito.doAnswer(invocation -> {
                알림_스레드.set(Thread.currentThread().getName());
                if (첫이벤트_완료대기.getCount() > 0) {
                    첫이벤트_완료대기.countDown();
                } else {
                    두번째이벤트_완료대기.countDown();
                }
                return null;
            }).when(processorUseCase).sendNotification(eq(walkRecord), any(WalkRangeStatus.class));

            이름있는_스레드에서_실행한다("bootstrap-thread", () ->
                    pipeline.send(위치메시지를_생성한다(1L, 37.1, 127.1), "1"));
            assertTrue(첫이벤트_완료대기.await(3, TimeUnit.SECONDS));

            이동검증_스레드.set(null);
            저장_스레드.set(null);
            범위계산_스레드.set(null);
            알림_스레드.set(null);

            시간을_앞당긴다(2, TimeUnit.SECONDS);

            이름있는_스레드에서_실행한다("stomp-thread", () ->
                    pipeline.send(위치메시지를_생성한다(1L, 37.2, 127.2), "1"));

            assertTrue(두번째이벤트_완료대기.await(3, TimeUnit.SECONDS));
            assertEquals("location-pipeline-thread", 이동검증_스레드.get());
            assertEquals("location-io-thread", 저장_스레드.get());
            assertEquals("location-io-thread", 범위계산_스레드.get());
            assertEquals("location-io-thread", 알림_스레드.get());
        } finally {
            pipelineExecutor.shutdownNow();
            ioExecutor.shutdownNow();
        }
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

    private void 테스트용_스케줄러를_재구성한다(Executor pipelineExecutor, ExecutorService ioExecutor) {
        RxJavaPlugins.reset();
        testScheduler = new TestScheduler();
        Scheduler ioScheduler = Schedulers.from(ioExecutor);
        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> testScheduler);
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> ioScheduler);
        pipeline = new LocationPipeline(walkRecordUseCaseProvider, processorUseCase, pipelineExecutor);
    }

    private void 이름있는_스레드에서_실행한다(String threadName, Runnable runnable) throws InterruptedException {
        Thread thread = new Thread(runnable, threadName);
        thread.start();
        thread.join();
    }

    private LocationMessage 위치메시지를_생성한다(Long walkRecordId, double latitude, double longitude) {
        return LocationMessage.builder()
                .walkRecordId(walkRecordId)
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }
}
