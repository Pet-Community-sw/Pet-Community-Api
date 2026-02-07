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

    /**
     * 5초마다 mq전송 실패한 outbox 이벤트를 찾아서 재전송
     * OutboxStatus를 SENDING으로 변경하여 중복 전송 방지
     * mq서버가 다운되었을 때 전송하면 SENDING 상태 계속 유지가 될 수 있으므로
     * SENDING이고 일정 시간이 지난 이벤트는 실패로 간주하고 다시 재전송.
     */
    @Scheduled(cron = "*/5 * * * * *")
    @Transactional
    public void scheduleOutboxEvent() {
        useCase.findAllFailed().forEach(event -> {
            event.setOutboxStatus(OutboxStatus.SENDING);
            publisher.publishEvent(event);
        });

    }
}
