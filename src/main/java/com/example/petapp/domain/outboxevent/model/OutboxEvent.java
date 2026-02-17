package com.example.petapp.domain.outboxevent.model;

import com.example.petapp.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
//@Table(indexes = {
//        @Index(name = "idx_outbox_event_status", columnList = "outboxStatus")
//})
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OutboxEvent extends BaseEntity {

    //cdc에서는 상태 필요 x
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    @Getter
//    private OutboxEventType outboxEventType;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    @Setter
//    @Getter
//    private OutboxStatus outboxStatus;


    @Column(nullable = false)
    private String aggregateid; //debezium 싱크

    @Column(nullable = false)
    private String aggregatetype;

    @Column(nullable = false, columnDefinition = "json") //저장에 유연
    @Getter
    private String payload;

}
