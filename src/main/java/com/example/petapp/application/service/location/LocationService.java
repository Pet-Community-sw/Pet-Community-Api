package com.example.petapp.application.service.location;

import com.example.petapp.application.common.HaversineUtil;
import com.example.petapp.application.in.chatting.model.dto.SendResponseDto;
import com.example.petapp.application.in.chatting.model.type.CommandType;
import com.example.petapp.application.in.location.LocationUseCase;
import com.example.petapp.application.in.location.dto.request.LocationMessage;
import com.example.petapp.application.in.location.mapper.LocationMapper;
import com.example.petapp.application.in.notification.dto.NotificationEvent;
import com.example.petapp.application.in.walkrecord.WalkRecordQueryUseCase;
import com.example.petapp.application.in.walkrecord.dto.request.SendLocationDto;
import com.example.petapp.application.out.SendPort;
import com.example.petapp.application.out.cache.LocationCachePort;
import com.example.petapp.domain.walkrecord.model.WalkRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class LocationService implements LocationUseCase {//예외 처리해야됨.

    private final LocationCachePort port;
    private final WalkRecordQueryUseCase walkRecordQueryUseCase;
    private final SendPort sendPort;
    private final ApplicationEventPublisher eventPublisher;
    
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

        sendPort.send(
                "/sub/walk-record/location/" + locationMessage.getWalkRecordId(),
                SendResponseDto.builder().commandType(CommandType.LOCATION).body(locationMessage).build());
    }

    private void sendLocationAndNotification(WalkRecord walkRecord, SendLocationDto sendLocationDto) {
        double distanceInMeters = HaversineUtil.calculateDistanceInMeters(sendLocationDto.getLocationLatitude(), sendLocationDto.getLocationLongitude(), sendLocationDto.getWalkerLatitude(), sendLocationDto.getWalkerLongitude());
        if (distanceInMeters >= walkRecord.getDelegateWalkPost().getAllowedRadiusMeters()) {
            log.warn("대리산책자가 산책범위에 벗어남.");
            eventPublisher.publishEvent(new NotificationEvent(walkRecord.getDelegateWalkPost().getProfile().getMember().getId(),
                    "위험! " + walkRecord.getMember().getName() + "님이 산책범위에 벗어났습니다. 현재 위치는 기준 지점에서 약 "
                            + distanceInMeters + "m 떨어져있습니다."));//distanceInMeters는 AOP당시 못받음.
            eventPublisher.publishEvent(new NotificationEvent(walkRecord.getMember().getId(),
                    "위험! 산책범위에 벗어났습니다. 산책 범위에 들어가주세요."));
        }
    }
}
