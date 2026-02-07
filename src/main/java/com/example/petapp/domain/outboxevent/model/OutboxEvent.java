package com.example.petapp.domain.outboxevent.model;

import com.example.petapp.domain.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(indexes = {
        @Index(name = "idx_outbox_event_status", columnList = "outboxStatus")
})
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OutboxEvent extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Getter
    private OutboxEventType outboxEventType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Setter
    private OutboxStatus outboxStatus;

    @Column(nullable = false)
    private Long aggregateId;

    @Column(nullable = false, columnDefinition = "json") //저장에 유연
    @Getter
    private String payload;

}
