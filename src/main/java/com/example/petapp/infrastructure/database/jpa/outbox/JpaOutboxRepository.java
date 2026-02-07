package com.example.petapp.infrastructure.database.jpa.outbox;

import com.example.petapp.domain.outboxevent.model.OutboxEvent;
import com.example.petapp.domain.outboxevent.model.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaOutboxRepository extends JpaRepository<OutboxEvent, Long> {
    List<OutboxEvent> findAllByOutboxStatus(OutboxStatus outboxStatus);
}
