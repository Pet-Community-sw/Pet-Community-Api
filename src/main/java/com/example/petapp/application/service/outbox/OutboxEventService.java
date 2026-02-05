package com.example.petapp.application.service.outbox;

import com.example.petapp.application.in.outbox.OutboxEventUseCase;
import com.example.petapp.domain.outboxevent.OutboxEventRepository;
import com.example.petapp.domain.outboxevent.model.OutboxEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OutboxEventService implements OutboxEventUseCase {

    private final OutboxEventRepository repository;

    @Override
    public void save(OutboxEvent outboxEvent) {
        repository.save(outboxEvent);
    }
}
