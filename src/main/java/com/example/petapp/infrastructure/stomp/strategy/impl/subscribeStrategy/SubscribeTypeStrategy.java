package com.example.petapp.infrastructure.stomp.strategy.impl.subscribeStrategy;

import com.example.petapp.infrastructure.stomp.SubscribeInfo;

public interface SubscribeTypeStrategy {
    boolean isHandler(String destination);

    void handle(SubscribeInfo subscribeInfo);
}
