package com.example.PetApp.infrastructure.stomp.strategy.impl.subscribeStrategy;

import com.example.PetApp.infrastructure.stomp.SubscribeInfo;

public interface SubscribeTypeStrategy {
    boolean isHandler(String destination);

    void handle(SubscribeInfo subscribeInfo);
}
