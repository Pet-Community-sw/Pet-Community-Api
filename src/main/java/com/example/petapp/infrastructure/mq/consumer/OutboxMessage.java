package com.example.petapp.infrastructure.mq.consumer;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class OutboxMessage {

    private Long id;

    private String payload;
}
