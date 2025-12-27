package com.example.petapp.infrastructure.stomp.strategy.subscribe;

import com.example.petapp.infrastructure.stomp.dto.SubscribeInfo;

public interface SubscribeTypeStrategy {
    boolean isHandler(String destination);

    void handle(SubscribeInfo subscribeInfo);
}
