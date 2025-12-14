package com.example.petapp.application.service.location;

import com.example.petapp.application.in.location.LocationUseCase;
import com.example.petapp.application.in.location.dto.request.LocationMessage;
import com.example.petapp.application.in.location.mapper.LocationMapper;
import com.example.petapp.application.in.notification.NotificationUseCase;
import com.example.petapp.application.in.walkrecord.WalkRecordQueryUseCase;
import com.example.petapp.application.in.walkrecord.dto.request.SendLocationDto;
import com.example.petapp.application.out.cache.LocationCachePort;
import com.example.petapp.common.base.util.HaversineUtil;
import com.example.petapp.domain.walkrecord.model.WalkRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class LocationService implements LocationUseCase {//예외 처리해야됨.

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final NotificationUseCase notificationUseCase;
    private final LocationCachePort port;
    private final WalkRecordQueryUseCase walkRecordQueryUseCase;

    @Override
    public void sendLocation(LocationMessage locationMessage, String memberId) {
        WalkRecord walkRecord = walkRecordQueryUseCase.findOrThrow(locationMessage.getWalkRecordId());
        walkRecord.validateMember(Long.valueOf(memberId));
        walkRecord.validateStart();

        SendLocationDto sendLocationDto = LocationMapper.toSendLocationDto(walkRecord, locationMessage);

        locationRedis(locationMessage, sendLocationDto);

        sendLocationAndNotification(walkRecord, sendLocationDto);
    }

    private void locationRedis(LocationMessage locationMessage, SendLocationDto sendLocationDto) {
        String location = sendLocationDto.getWalkerLongitude() + "," + sendLocationDto.getWalkerLatitude();
        port.create(locationMessage.getWalkRecordId(), location);

        simpMessagingTemplate.convertAndSend(
                "/sub/walk-record/location/" + locationMessage.getWalkRecordId(),
                locationMessage);
    }

    private void sendLocationAndNotification(WalkRecord walkRecord, SendLocationDto sendLocationDto) {
        double distanceInMeters = HaversineUtil.calculateDistanceInMeters(sendLocationDto.getLocationLatitude(), sendLocationDto.getLocationLongitude(), sendLocationDto.getWalkerLatitude(), sendLocationDto.getWalkerLongitude());
        if (distanceInMeters >= walkRecord.getDelegateWalkPost().getAllowedRadiusMeters()) {
            log.warn("대리산책자가 산책범위에 벗어남.");
            notificationUseCase.send(walkRecord.getDelegateWalkPost().getProfile().getMember(),
                    "위험! " + walkRecord.getMember().getName() + "님이 산책범위에 벗어났습니다. 현재 위치는 기준 지점에서 약 "
                            + distanceInMeters + "m 떨어져있습니다.");//distanceInMeters는 AOP당시 못받음.
            notificationUseCase.send(walkRecord.getMember(),
                    "위험! 산책범위에 벗어났습니다. 산책 범위에 들어가주세요.");
        }
    }
}
