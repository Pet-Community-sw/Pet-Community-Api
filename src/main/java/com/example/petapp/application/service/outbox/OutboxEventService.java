package com.example.petapp.application.service.outbox;

import com.example.petapp.application.usecase.outbox.OutboxEventUseCase;
import com.example.petapp.domain.outboxevent.OutboxEventRepository;
import com.example.petapp.domain.outboxevent.model.OutboxEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OutboxEventService implements OutboxEventUseCase {

    private final OutboxEventRepository repository;

    @Override
    public OutboxEvent save(OutboxEvent outboxEvent) {
        return repository.save(outboxEvent);
    }
}
