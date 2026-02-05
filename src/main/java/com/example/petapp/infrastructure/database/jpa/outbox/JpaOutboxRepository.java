package com.example.petapp.infrastructure.database.jpa.outbox;

import com.example.petapp.domain.outboxevent.model.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaOutboxRepository extends JpaRepository<OutboxEvent, Long> {
}
