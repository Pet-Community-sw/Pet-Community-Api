package com.example.petapp.domain.outboxevent;

import com.example.petapp.domain.outboxevent.model.OutboxEvent;

import java.util.Optional;

public interface OutboxEventRepository {
    OutboxEvent save(OutboxEvent outboxEvent);

    Optional<OutboxEvent> find(Long outboxId);
}
