package com.example.PetApp.infrastructure.stomp.strategy.impl.subscribeStrategy;

import com.example.PetApp.infrastructure.stomp.SubscribeInfo;
import org.springframework.util.AntPathMatcher;

import java.util.Map;

public abstract class BaseSubscribeTypeStrategy implements SubscribeTypeStrategy {
    protected static final AntPathMatcher PATH = new AntPathMatcher();

    protected Map<String, String> patternMap(String pattern, String destination) {
        if (!PATH.match(pattern, destination)) throw new IllegalArgumentException("패턴 불일치");
        return PATH.extractUriTemplateVariables(pattern, destination);
    }

    protected Long principalId(SubscribeInfo subscribeInfo) {
        return Long.valueOf(subscribeInfo.getPrincipal().getName());
    }
}

