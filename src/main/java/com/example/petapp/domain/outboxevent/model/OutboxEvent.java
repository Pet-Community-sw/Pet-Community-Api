package com.example.petapp.domain.outboxevent.model;

import com.example.petapp.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity

@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OutboxEvent extends BaseEntity {

    @Column(nullable = false)
    private String routingKey;

    @Column(nullable = false, columnDefinition = "json") //저장에 유연
    @Getter
    private String payload;

}
