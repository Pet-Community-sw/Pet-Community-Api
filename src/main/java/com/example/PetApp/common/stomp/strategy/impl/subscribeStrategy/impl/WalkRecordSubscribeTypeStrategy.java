package com.example.PetApp.common.stomp.strategy.impl.subscribeStrategy.impl;

import com.example.PetApp.common.stomp.SubscribeInfo;
import com.example.PetApp.common.stomp.strategy.impl.subscribeStrategy.BaseSubscribeTypeStrategy;
import com.example.PetApp.domain.query.QueryService;
import com.example.PetApp.domain.walkrecord.model.entity.WalkRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/*
 *  todo : 다시 한 번 봐야됨.
 *
 * */
@Component
@RequiredArgsConstructor
@Slf4j
public class WalkRecordSubscribeTypeStrategy extends BaseSubscribeTypeStrategy {

    private static final String PATTERN = "/sub/walk-record/location/{walkRecordId}";

    private final QueryService queryService;

    @Override
    public boolean isHandler(String destination) {
        return PATH.match(PATTERN, destination);
    }

    @Override
    public void handle(SubscribeInfo subscribeInfo) {
        Map<String, String> map = patternMap(PATTERN, subscribeInfo.getDestination());
        Long walkRecordId = Long.valueOf(map.get("walkRecordId"));
        Long memberId = principalId(subscribeInfo);

        WalkRecord walkRecord = queryService.findByWalkRecord(walkRecordId);
        Long ownerMemberId = walkRecord.getDelegateWalkPost().getProfile().getMember().getId();

        if (!ownerMemberId.equals(memberId)) {
            throw new IllegalArgumentException("잘못된 접근입니다.");
        }
        //todo : 이거 redis 없냐?

        log.info("[STOMP] 구독 walkRecordId: {}, memberId: {}", walkRecordId, memberId);
    }
}
