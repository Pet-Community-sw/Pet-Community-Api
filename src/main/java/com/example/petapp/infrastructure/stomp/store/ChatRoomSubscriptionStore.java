package com.example.petapp.infrastructure.stomp.store;

import java.util.Optional;

public interface ChatRoomSubscriptionStore {

    void deleteChatRoomSubscription(String subscriptionId);

    void createChatRoomSubscription(String subscriptionId, Long chatRoomId);

    Optional<Long> getChatRoomIdBySubscriptionId(String subscriptionId);
}
