package com.example.petapp.application.out;

import com.example.petapp.application.in.email.EventEmail;

public interface EmailSendPort {

    void send(EventEmail eventEmail);
}
