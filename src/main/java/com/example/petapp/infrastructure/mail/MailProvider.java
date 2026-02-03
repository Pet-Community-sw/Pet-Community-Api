package com.example.petapp.infrastructure.mail;

import com.example.petapp.application.in.email.EventEmail;

public interface MailProvider {
    void send(EventEmail event);
}
