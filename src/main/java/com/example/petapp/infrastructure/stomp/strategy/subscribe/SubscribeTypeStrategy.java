package com.example.petapp.infrastructure.stomp.strategy.subscribe;

import com.example.petapp.infrastructure.stomp.dto.SubscribeInfo;
import org.springframework.util.AntPathMatcher;

import java.util.Map;

public abstract class SubscribeTypeStrategy {
    protected static final AntPathMatcher PATH = new AntPathMatcher();

    public abstract boolean isHandler(String destination);

    public abstract void handle(SubscribeInfo subscribeInfo);

    protected Map<String, String> pathMap(String pattern, String destination) {
        if (!PATH.match(pattern, destination)) throw new IllegalArgumentException("패턴 불일치");
        return PATH.extractUriTemplateVariables(pattern, destination);
    }
}
