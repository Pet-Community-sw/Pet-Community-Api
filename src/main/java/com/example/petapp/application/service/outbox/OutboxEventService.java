package com.example.petapp.application.service.outbox;

import com.example.petapp.application.in.outbox.OutboxEventUseCase;
import com.example.petapp.domain.outboxevent.OutboxEventRepository;
import com.example.petapp.domain.outboxevent.model.OutboxEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OutboxEventService implements OutboxEventUseCase {

    private final OutboxEventRepository repository;

    @Override
    public OutboxEvent save(OutboxEvent outboxEvent) {
        return repository.save(outboxEvent);
    }

    //cdc로 변경
//    @Transactional
//    @Override
//    public void update(Long outboxId, OutboxStatus outboxStatus) {
//        repository.find(outboxId).ifPresent(event -> event.setOutboxStatus(outboxStatus));
//    }

    @Override
    public List<OutboxEvent> findAllFailed() {

        return repository.findAllFailed();
    }
}
