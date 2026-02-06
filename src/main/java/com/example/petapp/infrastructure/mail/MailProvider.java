package com.example.petapp.infrastructure.mail;

import com.example.petapp.application.in.email.EmailEvent;

public interface MailProvider {
    void send(EmailEvent event);
}
