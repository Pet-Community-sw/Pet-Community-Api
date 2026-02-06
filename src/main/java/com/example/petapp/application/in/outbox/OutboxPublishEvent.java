package com.example.petapp.application.in.outbox;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class OutboxPublishEvent {
    private Long outboxId;
    private Object payload;
}
