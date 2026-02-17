package com.example.petapp.application.in.member;

import com.example.petapp.infrastructure.mq.consumer.OutboxMessage;

public interface MemberSearchUseCase {
    void handle(OutboxMessage outboxMessage);
}
