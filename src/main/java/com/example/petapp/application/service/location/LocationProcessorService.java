package com.example.petapp.application.service.location;

import com.example.petapp.application.common.HaversineUtil;
import com.example.petapp.application.in.chatting.model.dto.SendResponseDto;
import com.example.petapp.application.in.chatting.model.type.CommandType;
import com.example.petapp.application.in.location.LocationProcessorUseCase;
import com.example.petapp.application.in.location.dto.request.LocationMessage;
import com.example.petapp.application.in.notification.dto.NotificationEvent;
import com.example.petapp.application.out.SendPort;
import com.example.petapp.application.out.cache.LocationCachePort;
import com.example.petapp.application.service.location.object.LastPoint;
import com.example.petapp.application.service.location.object.WalkRangeStatus;
import com.example.petapp.domain.walkrecord.model.WalkRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
@RequiredArgsConstructor
public class LocationProcessorService implements LocationProcessorUseCase {

    private static final long MIN_MOVE_METERS = 3;

    private final LocationCachePort port;
    private final ApplicationEventPublisher eventPublisher;
    private final SendPort sendPort;


    private final Map<Long, LastPoint> lastPointMap = new ConcurrentHashMap<>();


    @Override
    public void sendNotification(WalkRecord walkRecord, WalkRangeStatus state) {

        if (state.isOutOfRange()) {
            eventPublisher.publishEvent(new NotificationEvent(
                    walkRecord.getDelegateWalkPost().getProfile().getMember().getId(),
                    "위험! " + walkRecord.getMember().getName() + "님이 산책범위에 벗어났습니다. 현재 위치는 기준 지점에서 약 "
                            + state.distance() + "m 떨어져있습니다."
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


    @Override
    public WalkRangeStatus checkRange(WalkRecord walkRecord, LocationMessage message) {
        double distance = HaversineUtil.calculateDistanceInMeters(
                walkRecord.getDelegateWalkPost().getLocation().getLocationLatitude(),
                walkRecord.getDelegateWalkPost().getLocation().getLocationLongitude(),
                message.getLatitude(),
                message.getLongitude()
        );
        boolean isOutOfRange = distance > walkRecord.getDelegateWalkPost().getAllowedRadiusMeters();
        return new WalkRangeStatus(distance, isOutOfRange);
    }


    @Override
    public void saveAndBroadcast(LocationMessage message) {
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
    @Override
    public boolean isEnoughMove(LocationMessage message) {
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


    @Override
    public void clean(Long walkRecordId) {
        lastPointMap.remove(walkRecordId);
    }
}
