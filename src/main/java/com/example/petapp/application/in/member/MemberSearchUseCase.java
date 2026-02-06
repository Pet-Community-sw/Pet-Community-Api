package com.example.petapp.application.in.member;

import com.example.petapp.domain.outboxevent.model.OutboxEvent;

public interface MemberSearchUseCase {
    void handle(OutboxEvent event);
}
