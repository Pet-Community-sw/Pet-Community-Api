package com.example.petapp.infrastructure.mail;

import com.example.petapp.infrastructure.mq.consumer.OutboxMessage;

public interface MailProvider {
    void send(OutboxMessage outboxMessage);
}
