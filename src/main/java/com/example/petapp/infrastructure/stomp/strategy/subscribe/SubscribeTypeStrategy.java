package com.example.petapp.infrastructure.stomp.strategy.subscribe;

import com.example.petapp.infrastructure.stomp.dto.SubscribeInfo;
import org.springframework.util.AntPathMatcher;

import java.util.Map;

public abstract class SubscribeTypeStrategy {
    protected static final AntPathMatcher PATH = new AntPathMatcher();

    public abstract boolean supports(String destination);

    public abstract void handle(SubscribeInfo subscribeInfo);

    protected Map<String, String> extractPathVariables(String pattern, String destination) {
        return PATH.extractUriTemplateVariables(pattern, destination);
    }
}
