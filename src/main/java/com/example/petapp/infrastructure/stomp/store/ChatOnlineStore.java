package com.example.petapp.infrastructure.stomp.store;

import java.util.Set;

public interface ChatOnlineStore {

    void createOnlineUser(Long chatroomId, Long memberId);

    void deleteOnlineUser(Long chatRoomId, Long memberId);

    Set<Long> getOnlineUserList(Long chatRoomId);
}
