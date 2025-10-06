package com.example.PetApp.common.stomp.strategy.command.impl.subscribeStrategy.impl;

import com.example.PetApp.common.stomp.SubscribeInfo;
import com.example.PetApp.common.stomp.strategy.command.impl.subscribeStrategy.BaseSubscribeTypeStrategy;
import com.example.PetApp.domain.member.model.entity.Member;
import com.example.PetApp.domain.memberchatRoom.model.entity.MemberChatRoom;
import com.example.PetApp.domain.query.QueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class OneToOneSubscribeTypeStrategy extends BaseSubscribeTypeStrategy {

    private static final String PATTERN = "/user/chat/{memberChatRoomId}";

    private final QueryService queryService;
    private final StringRedisTemplate redisTemplate;

    @Override
    public boolean isHandler(String destination) {
        return PATH.match(PATTERN, destination);
    }

    @Override
    public void handle(SubscribeInfo subscribeInfo) {
        Map<String, String> map = patternMap(PATTERN, subscribeInfo.getDestination());
        Long memberChatRoomId = Long.valueOf(map.get("memberChatRoomId"));
        Long memberId = principalId(subscribeInfo, "memberId");

        Member member = queryService.findByMember(memberId);
        MemberChatRoom memberChatRoom = queryService.findByMemberChatRoom(memberChatRoomId);

        if (!memberChatRoom.getMembers().contains(member)) {
            throw new IllegalArgumentException("잘못된 접근입니다.");
        }

        redisTemplate.opsForSet().add("memberChatRoomId:"+memberChatRoomId+":onlineMembers", memberId.toString());

        log.info("[STOMP] 구독 memberChatRoomId: {}, memberId: {}", memberChatRoomId, memberId);
    }
}
