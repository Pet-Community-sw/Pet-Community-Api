package com.example.petapp.infrastructure.database.jpa.outbox;

import com.example.petapp.domain.outboxevent.OutboxEventRepository;
import com.example.petapp.domain.outboxevent.model.OutboxEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OutboxEventRepositoryImpl implements OutboxEventRepository {

    private final JpaOutboxRepository repository;

    @Override
    public OutboxEvent save(OutboxEvent outboxEvent) {
        return repository.save(outboxEvent);
    }

    @Override
    public Optional<OutboxEvent> find(Long outboxId) {
        return repository.findById(outboxId);
    }
}
