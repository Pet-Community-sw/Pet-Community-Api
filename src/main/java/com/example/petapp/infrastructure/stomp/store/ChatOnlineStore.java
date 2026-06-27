package com.example.petapp.infrastructure.stomp.store;

import java.util.Set;

public interface ChatOnlineStore {

    void addOnlineUser(Long chatroomId, Long memberId);

    void removeOnlineUser(Long chatRoomId, Long memberId);

    Set<Long> getOnlineUserIds(Long chatRoomId);
}
