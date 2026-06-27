package com.example.petapp.infrastructure.stomp.strategy.subscribe.impl;

import com.example.petapp.application.common.exception.ErrorCode;
import com.example.petapp.application.common.exception.PetCommunityException;
import com.example.petapp.domain.walkrecord.WalkRecordRepository;
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

    private static final String KEY = "walkRecordId";
    private static final String PATTERN = "/sub/walk/{" + KEY + "}";

    private final WalkRecordRepository walkRecordRepository;

    @Override
    public boolean supports(String destination) {
        return PATH.match(PATTERN, destination);
    }

    @Override
    public void handle(SubscribeInfo subscribeInfo) {
        Map<String, String> map = extractPathVariables(PATTERN, subscribeInfo.getDestination());
        Long walkRecordId = Long.valueOf(map.get(KEY));
        Long memberId = Long.valueOf(subscribeInfo.getPrincipal().getName());

        WalkRecord walkRecord = walkRecordRepository.find(walkRecordId)
                .orElseThrow(() -> new PetCommunityException(ErrorCode.NOT_FOUND, "해당 산책기록은 없습니다."));
        Long ownerMemberId = walkRecord.getDelegateWalkPost().getProfile().getMember().getId();

        if (!ownerMemberId.equals(memberId)) {
            throw new PetCommunityException(ErrorCode.FORBIDDEN, "해당 산책 기록에 접근할 권한이 없습니다.");
        }

        log.info("[STOMP] 구독 walkRecordId: {}, id: {}", walkRecordId, memberId);
    }
}
