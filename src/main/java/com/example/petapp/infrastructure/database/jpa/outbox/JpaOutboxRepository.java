package com.example.petapp.infrastructure.database.jpa.outbox;

import com.example.petapp.domain.outboxevent.model.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JpaOutboxRepository extends JpaRepository<OutboxEvent, Long> {
    @Query(value = "select * from outbox_event " +
            "where outbox_status= 'PENDING' " +
            "or (outbox_status = 'SENDING' and created_at < now() -  interval 10 second)",
            nativeQuery = true
    )
    List<OutboxEvent> findAllFailed();
}
