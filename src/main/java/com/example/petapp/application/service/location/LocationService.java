package com.example.petapp.application.service.location;

import com.example.petapp.application.common.HaversineUtil;
import com.example.petapp.application.in.chatting.model.dto.SendResponseDto;
import com.example.petapp.application.in.chatting.model.type.CommandType;
import com.example.petapp.application.in.location.LocationUseCase;
import com.example.petapp.application.in.location.dto.request.LocationMessage;
import com.example.petapp.application.in.notification.dto.NotificationEvent;
import com.example.petapp.application.in.walkrecord.WalkRecordQueryUseCase;
import com.example.petapp.application.out.SendPort;
import com.example.petapp.application.out.cache.LocationCachePort;
import com.example.petapp.domain.walkrecord.model.WalkRecord;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class LocationService implements LocationUseCase {//예외 처리해야됨.

    private static final long TIMEOUT_MINUTES = 10;
    private static final long THROTTLE_SECONDS = 2;
    private static final long MIN_MOVE_METERS = 3;
    private final LocationCachePort port;
    private final WalkRecordQueryUseCase walkRecordQueryUseCase;
    private final SendPort sendPort;
    private final ApplicationEventPublisher eventPublisher;
    private final Map<Long, Subject<LocationMessage>> subjectMap = new ConcurrentHashMap<>();
    private final Map<Long, Disposable> pipelineMap = new ConcurrentHashMap<>();
    private final Map<Long, LastPoint> lastPointMap = new ConcurrentHashMap<>();

    @Override
    public void sendLocation(LocationMessage message, String memberId) {
        Subject<LocationMessage> subject = subjectMap.computeIfAbsent(message.getWalkRecordId(), id -> {
            //사용자 검증
            WalkRecord walkRecord = walkRecordQueryUseCase.findOrThrow(message.getWalkRecordId());
            walkRecord.validateMember(Long.valueOf(memberId));
            walkRecord.validateStart();

            /**
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
                .filter(this::isEnoughMove)
                .concatMap(message -> Flowable.fromCallable(() -> {
                                    saveAndBroadcast(message);
                                    return message;
                                }).subscribeOn(Schedulers.io())
                                .onErrorResumeNext(err -> {
                                    log.error("saveAndBroadcast error walkRecordId={}", walkRecord.getId(), err);
                                    return Flowable.empty();
                                })//io작업 중 에러처리
                )
                .map(message -> checkRange(walkRecord, message))
                .distinctUntilChanged((a, b) -> a.isOutOfRange() == b.isOutOfRange())
                .doOnNext(state -> sendNotification(walkRecord, state))
                .subscribe(
                        ok -> {
                        },
                        err -> log.warn("Location pipeline error walkRecordId={}", walkRecord.getId(), err),
                        () -> cleanup(walkRecord.getId())
                );

        pipelineMap.put(walkRecord.getId(), disposable);
        log.info("Location Pipeline start walkRecordId: {} ", walkRecord.getId());

    }

    private void sendNotification(WalkRecord walkRecord, WalkRangeStatus state) {

        if (state.isOutOfRange()) {
            eventPublisher.publishEvent(new NotificationEvent(
                    walkRecord.getDelegateWalkPost().getProfile().getMember().getId(),
                    "위험! " + walkRecord.getMember().getName() + "님이 산책범위에 벗어났습니다. 현재 위치는 기준 지점에서 약 "
                            + (long) state.distance() + "m 떨어져있습니다."
            ));

            eventPublisher.publishEvent(new NotificationEvent(
                    walkRecord.getMember().getId(),
                    "위험! 산책범위에 벗어났습니다. 산책 범위에 들어가주세요."
            ));
        } else {
            eventPublisher.publishEvent(new NotificationEvent(
                    walkRecord.getDelegateWalkPost().getProfile().getMember().getId(),
                    walkRecord.getMember().getName() + "님이 산책 범위로 복귀했습니다."
            ));

            eventPublisher.publishEvent(new NotificationEvent(
                    walkRecord.getMember().getId(),
                    "산책 범위로 복귀했습니다."
            ));
        }

    }


    private WalkRangeStatus checkRange(WalkRecord walkRecord, LocationMessage message) {
        double distance = HaversineUtil.calculateDistanceInMeters(
                walkRecord.getDelegateWalkPost().getLocation().getLocationLatitude(),
                walkRecord.getDelegateWalkPost().getLocation().getLocationLongitude(),
                message.getLatitude(),
                message.getLongitude()
        );
        boolean isOutOfRange = distance > walkRecord.getDelegateWalkPost().getAllowedRadiusMeters();
        return new WalkRangeStatus(distance, isOutOfRange);
    }


    private void saveAndBroadcast(LocationMessage message) {
        String location = message.getLongitude() + "," + message.getLatitude();
        port.create(message.getWalkRecordId(), location);

        sendPort.send(
                "/sub/walk-record/location/" + message.getWalkRecordId(),
                SendResponseDto.builder().commandType(CommandType.LOCATION).body(message).build());
    }

    /**
     * 이동 거리가 최소 거리 이상인지 확인
     * 1m씩 이동해도 저장하지말고 이전 저장 지점 대비 3m이상 일 때 저장
     * 1m씩 여러번 이동하는 경우를 방지
     */
    private boolean isEnoughMove(LocationMessage message) {
        LastPoint lastPoint = lastPointMap.get(message.getWalkRecordId());

        if (lastPoint == null) {//최초 위치는 통과
            lastPointMap.put(message.getWalkRecordId(), new LastPoint(message.getLatitude(), message.getLongitude()));
            return true;
        }

        double distanceInMeters = HaversineUtil.calculateDistanceInMeters(lastPoint.latitude(), lastPoint.longitude(), message.getLatitude(), message.getLongitude());

        if (distanceInMeters >= MIN_MOVE_METERS) {
            lastPointMap.put(message.getWalkRecordId(), new LastPoint(message.getLatitude(), message.getLongitude()));
            return true;
        }
        return false;
    }

    private void cleanup(Long walkRecordId) {
        Disposable d = pipelineMap.remove(walkRecordId);
        if (d != null && !d.isDisposed()) d.dispose();

        subjectMap.remove(walkRecordId);
        lastPointMap.remove(walkRecordId);

        log.info("Location pipeline cleaned up for walkRecordId={}", walkRecordId);
    }
}
