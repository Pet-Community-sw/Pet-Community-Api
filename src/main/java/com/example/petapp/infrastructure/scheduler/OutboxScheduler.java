package com.example.petapp.infrastructure.scheduler;

import com.example.petapp.application.in.outbox.OutboxEventUseCase;
import com.example.petapp.domain.outboxevent.model.OutboxStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxScheduler {

    private final OutboxEventUseCase useCase;
    private final ApplicationEventPublisher publisher;

    @Scheduled(cron = "*/5 * * * * *")
    @Transactional
    public void scheduleOutboxEvent() {
        useCase.findByStatus(OutboxStatus.PENDING).forEach(publisher::publishEvent);
    }
}
