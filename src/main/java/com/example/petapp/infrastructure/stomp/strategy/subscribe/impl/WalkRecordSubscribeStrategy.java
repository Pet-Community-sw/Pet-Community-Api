package com.example.petapp.infrastructure.stomp.strategy.subscribe.impl;

import com.example.petapp.application.in.walkrecord.WalkRecordQueryUseCase;
import com.example.petapp.domain.walkrecord.model.WalkRecord;
import com.example.petapp.infrastructure.stomp.dto.SubscribeInfo;
import com.example.petapp.infrastructure.stomp.strategy.subscribe.SubscribeTypeStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class WalkRecordSubscribeStrategy extends SubscribeTypeStrategy {

    private static final String PATTERN = "/sub/walk/{walkRecordId}";

    private final WalkRecordQueryUseCase useCase;

    @Override
    public boolean isHandler(String destination) {
        return PATH.match(PATTERN, destination);
    }

    @Override
    public void handle(SubscribeInfo subscribeInfo) {
        Map<String, String> map = pathMap(PATTERN, subscribeInfo.getDestination());
        Long walkRecordId = Long.valueOf(map.get("walkRecordId"));
        Long memberId = Long.valueOf(subscribeInfo.getPrincipal().getName());

        WalkRecord walkRecord = useCase.findOrThrow(walkRecordId);
        Long ownerMemberId = walkRecord.getDelegateWalkPost().getProfile().getMember().getId();

        if (!ownerMemberId.equals(memberId)) {
            throw new IllegalArgumentException("잘못된 접근입니다.");
        }

        log.info("[STOMP] 구독 walkRecordId: {}, id: {}", walkRecordId, memberId);
    }
}
