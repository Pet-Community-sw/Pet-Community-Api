package com.example.petapp.infrastructure.database.adapter;

import com.example.petapp.domain.outboxevent.OutboxEventRepository;
import com.example.petapp.domain.outboxevent.model.OutboxEvent;
import com.example.petapp.infrastructure.database.jpa.outbox.JpaOutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OutboxEventRepositoryAdapter implements OutboxEventRepository {

    private final JpaOutboxRepository repository;

    @Override
    public OutboxEvent save(OutboxEvent outboxEvent) {
        return repository.save(outboxEvent);
    }

    @Override
    public Optional<OutboxEvent> find(Long outboxId) {
        return repository.findById(outboxId);
    }

    @Override
    public List<OutboxEvent> findAllFailed() {
        return repository.findAllFailed();
    }
}
