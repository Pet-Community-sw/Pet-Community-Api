package com.example.petapp.infrastructure.database.jpa.outbox;

import com.example.petapp.domain.outboxevent.OutboxEventRepository;
import com.example.petapp.domain.outboxevent.model.OutboxEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OutboxEventRepositoryImpl implements OutboxEventRepository {

    private final JpaOutboxRepository repository;

    @Override
    public void save(OutboxEvent outboxEvent) {
        repository.save(outboxEvent);
    }
}
