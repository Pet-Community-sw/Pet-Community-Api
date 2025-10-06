package com.example.PetApp.common.stomp.strategy.command.impl.subscribeStrategy;

import com.example.PetApp.common.stomp.SubscribeInfo;

public interface SubscribeTypeStrategy {
    boolean supports(String destination);

    void handle(SubscribeInfo subscribeInfo);
}
