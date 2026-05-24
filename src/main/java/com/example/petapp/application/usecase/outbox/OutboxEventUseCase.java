package com.example.petapp.application.usecase.outbox;

import com.example.petapp.domain.outboxevent.model.OutboxEvent;

public interface OutboxEventUseCase {
    OutboxEvent save(OutboxEvent outboxEvent);

}
