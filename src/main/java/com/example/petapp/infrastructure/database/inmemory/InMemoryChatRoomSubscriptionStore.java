package com.example.petapp.infrastructure.database.inmemory;

import com.example.petapp.infrastructure.stomp.store.ChatRoomSubscriptionStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
@RequiredArgsConstructor
public class InMemoryChatRoomSubscriptionStore implements ChatRoomSubscriptionStore {

    private final ConcurrentMap<String, Long> subscriptionMap = new ConcurrentHashMap<>();

    @Override
    public void deleteChatRoomSubscription(String subscriptionId) {
        subscriptionMap.remove(subscriptionId);
    }

    @Override
    public void createChatRoomSubscription(String subscriptionId, Long chatRoomId) {
        subscriptionMap.put(subscriptionId, chatRoomId);
    }

    @Override
    public Optional<Long> getChatRoomIdBySubscriptionId(String subscriptionId) {
        return Optional.ofNullable(subscriptionMap.get(subscriptionId));
    }

}
