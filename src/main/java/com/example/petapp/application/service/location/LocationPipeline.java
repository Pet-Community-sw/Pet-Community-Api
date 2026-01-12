package com.example.petapp.application.service.location;

import com.example.petapp.application.in.location.LocationProcessorUseCase;
import com.example.petapp.application.in.location.dto.request.LocationMessage;
import com.example.petapp.application.in.walkrecord.WalkRecordQueryUseCase;
import com.example.petapp.domain.walkrecord.model.WalkRecord;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class LocationPipeline {

    private static final long TIMEOUT_MINUTES = 10;
    private static final long THROTTLE_SECONDS = 2;

    private final WalkRecordQueryUseCase useCase;
    private final LocationProcessorUseCase processorUseCase;

    private final Map<Long, Subject<LocationMessage>> subjectMap = new ConcurrentHashMap<>();
    private final Map<Long, Disposable> pipelineMap = new ConcurrentHashMap<>();

    public void send(LocationMessage message, String memberId) {
        Subject<LocationMessage> subject = subjectMap.computeIfAbsent(message.getWalkRecordId(), id -> {
            //사용자 검증
            WalkRecord walkRecord = useCase.findOrThrow(message.getWalkRecordId());
            walkRecord.validateMember(Long.valueOf(memberId));
            walkRecord.validateStart();

            /*
             * Subject는 thread-safe이 아니라 동시성 이슈가 있을 수 있으므로 toSerialized()로 감싸서 사용해야 함
             * 락 + 큐 방식으로 동작하여 동시성 문제를 해결
             */
            Subject<LocationMessage> s = PublishSubject.<LocationMessage>create().toSerialized();
            startPipeline(walkRecord, s);
            return s;
        });
        //동시에 스레드가 onNext를 호출할 수 있음 그래서 toSerialized()
        subject.onNext(message);
    }

    private void startPipeline(WalkRecord walkRecord, Subject<LocationMessage> subject) {
        Disposable disposable = subject
                //백프레셔 이슈 발생 시 최신 데이터만 처리 서버가 죽는 것 보단 최신 데이터만 처리하는게 나아보임.
                .toFlowable(BackpressureStrategy.LATEST)
                .throttleFirst(THROTTLE_SECONDS, TimeUnit.SECONDS) //백프레셔 문제 없을 듯
                .observeOn(Schedulers.computation())
                .timeout(TIMEOUT_MINUTES, TimeUnit.MINUTES)
                .doOnError(error -> log.error("Location Pipeline Error walkRecordId: {}, error : {} ", walkRecord.getId(), error.toString()))
                .onErrorComplete()//스레드 i/o는 io 계산만 computation으로 해야함
                .filter(processorUseCase::isEnoughMove)
                .concatMap(message -> Flowable.fromCallable(() -> {
                                    processorUseCase.saveAndBroadcast(message);
                                    return message;
                                }).subscribeOn(Schedulers.io())
                                .onErrorResumeNext(err -> {
                                    log.error("saveAndBroadcast error walkRecordId={}", walkRecord.getId(), err);
                                    return Flowable.empty();
                                })//io작업 중 에러처리
                )
                .map(message -> processorUseCase.checkRange(walkRecord, message))
                .distinctUntilChanged((a, b) -> a.isOutOfRange() == b.isOutOfRange())
                .doOnNext(state -> processorUseCase.sendNotification(walkRecord, state))
                .subscribe(
                        ok -> {
                        },
                        err -> log.warn("Location pipeline error walkRecordId={}", walkRecord.getId(), err),
                        () -> cleanup(walkRecord.getId())
                );

        pipelineMap.put(walkRecord.getId(), disposable);
        log.info("Location Pipeline start walkRecordId: {} ", walkRecord.getId());

    }

    public void cleanup(Long walkRecordId) {
        Disposable d = pipelineMap.remove(walkRecordId);
        if (d != null && !d.isDisposed()) d.dispose();

        subjectMap.remove(walkRecordId);
        processorUseCase.cleanup(walkRecordId);

        log.info("Location pipeline clean walkRecordId={}", walkRecordId);
    }

}
