package com.example.petapp.application.in.outbox;

import com.example.petapp.domain.outboxevent.model.OutboxEvent;

public interface OutboxEventUseCase {
    void save(OutboxEvent outboxEvent);
}
