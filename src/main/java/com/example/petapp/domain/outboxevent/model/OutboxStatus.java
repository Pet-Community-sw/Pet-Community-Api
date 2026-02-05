package com.example.petapp.domain.outboxevent.model;

public enum OutboxStatus {
    PENDING, //재시도 해야하는 상태
    SENDING, //mq가 처리중인 상태
    COMPLETED //처리가 완료된 상태
}
