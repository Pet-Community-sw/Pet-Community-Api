package com.example.PetApp.common.stomp.strategy.command.impl.subscribeStrategy;

import com.example.PetApp.common.stomp.SubscribeInfo;

public interface SubscribeTypeStrategy {
    boolean isHandler(String destination);

    void handle(SubscribeInfo subscribeInfo);
}
