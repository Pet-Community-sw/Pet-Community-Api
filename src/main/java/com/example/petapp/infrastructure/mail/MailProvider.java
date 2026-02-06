package com.example.petapp.infrastructure.mail;

import com.example.petapp.domain.outboxevent.model.OutboxEvent;

public interface MailProvider {
    void send(OutboxEvent event);
}
