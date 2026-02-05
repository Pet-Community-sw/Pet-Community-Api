package com.example.petapp.domain.outboxevent;

import com.example.petapp.domain.outboxevent.model.OutboxEvent;

public interface OutboxEventRepository {
    void save(OutboxEvent outboxEvent);
}
