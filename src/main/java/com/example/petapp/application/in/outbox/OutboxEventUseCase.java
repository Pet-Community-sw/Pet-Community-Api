package com.example.petapp.application.in.outbox;

import com.example.petapp.domain.outboxevent.model.OutboxEvent;
import com.example.petapp.domain.outboxevent.model.OutboxStatus;

import java.util.List;

public interface OutboxEventUseCase {
    OutboxEvent save(OutboxEvent outboxEvent);

    void update(Long outboxId, OutboxStatus outboxStatus);

    List<OutboxEvent> findByStatus(OutboxStatus status);
}
