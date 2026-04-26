package com.example.petapp.application.service.location;

import com.example.petapp.application.in.location.LocationProcessorUseCase;
import com.example.petapp.application.in.location.dto.request.LocationMessage;
import com.example.petapp.application.in.walkrecord.WalkRecordQueryUseCase;
import com.example.petapp.application.service.location.object.PipelineContext;
import com.example.petapp.domain.walkrecord.model.WalkRecord;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class LocationPipeline {

    private static final long TIMEOUT_MINUTES = 10;//해당 파이프에 대해 10분동안 이벤트가 없으면 파이프 제거
    private static final long THROTTLE_SECONDS = 2;//많은 이벤트 중 2초에 한 번 씩 이벤트를 받음.

    private final WalkRecordQueryUseCase useCase;
    private final LocationProcessorUseCase processorUseCase;
    private final Executor locationPipelineExecutor;

    private final Map<Long, CompletableFuture<PipelineContext>> initMap = new ConcurrentHashMap<>();
    private final Map<Long, Disposable> pipelineMap = new ConcurrentHashMap<>();

    public void send(LocationMessage message, String memberId) {
        Long walkRecordId = message.getWalkRecordId();

        CompletableFuture<PipelineContext> future = initMap.computeIfAbsent(walkRecordId, id ->
                CompletableFuture.supplyAsync(() -> initPipeline(walkRecordId, memberId), locationPipelineExecutor));

        future.thenAcceptAsync(context -> {
            if (!Objects.equals(context.memberId(), memberId)) {
                log.error("LocationPipeline member mismatch walkRecordId={}, ownerMemberId={}, currentMemberId={}",
                        walkRecordId, context.memberId(), memberId);
                return;
            }
            context.subject().onNext(message);
        }, locationPipelineExecutor).exceptionally(e -> {
            initMap.remove(walkRecordId);
            log.error("LocationPipeline initial error walkRecordId={}", walkRecordId, e);
            return null;
        });

    }

    private PipelineContext initPipeline(Long walkRecordId, String memberId) {
        //파이프라인 초기화 전에 호출 시 매번 DB조회가 발생 함으로 여기에 위치
        WalkRecord walkRecord = useCase.findAndValidate(walkRecordId, Long.valueOf(memberId));

        /*
         * Subject는 thread-safe이 아니라 동시성 이슈가 있을 수 있으므로 toSerialized()로 감싸서 사용해야 함
         * 락 + 큐 방식으로 동작하여 동시성 문제를 해결
         */
        Subject<LocationMessage> subject = PublishSubject.<LocationMessage>create().toSerialized();
        startPipeline(walkRecord, subject);
        return new PipelineContext(memberId, subject);
    }

    private void startPipeline(WalkRecord walkRecord, Subject<LocationMessage> subject) {
        Disposable disposable = subject
                .throttleFirst(THROTTLE_SECONDS, TimeUnit.SECONDS)
                .timeout(TIMEOUT_MINUTES, TimeUnit.MINUTES)
                .filter(processorUseCase::isEnoughMove)
                .concatMap(message -> Observable.fromCallable(() -> {
                                    processorUseCase.saveAndSend(message);
                                    return message;
                                }).subscribeOn(Schedulers.io())
                                .onErrorResumeNext(err -> {
                                    log.error("saveAndBroadcast error walkRecordId={}", walkRecord.getId(), err);
                                    return Observable.empty();
                                })
                )
                .map(message -> processorUseCase.checkRange(walkRecord, message))
                .distinctUntilChanged((a, b) -> a.isOutOfRange() == b.isOutOfRange())
                .doOnNext(state -> processorUseCase.sendNotification(walkRecord, state))
                .subscribe(
                        ok -> {
                        },
                        err -> {
                            log.error("Location pipeline error walkRecordId={}", walkRecord.getId(), err);
                            clean(walkRecord.getId());
                        }
                );

        pipelineMap.put(walkRecord.getId(), disposable);
        log.info("Location Pipeline start walkRecordId: {} ", walkRecord.getId());

    }

    public void clean(Long walkRecordId) {
        Disposable d = pipelineMap.remove(walkRecordId);
        if (d != null && !d.isDisposed()) d.dispose();

        initMap.remove(walkRecordId);
        processorUseCase.clean(walkRecordId);

        log.info("Location pipeline clean walkRecordId={}", walkRecordId);
    }
}
