package com.example.petapp.service;

import com.example.petapp.application.common.JsonUtil;
import com.example.petapp.application.service.outbox.OutboxEventService;
import com.example.petapp.domain.outboxevent.model.OutboxEvent;
import com.example.petapp.domain.outboxevent.model.OutboxEventType;
import com.example.petapp.domain.outboxevent.model.OutboxStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class OutboxTest {

    @Autowired
    public JsonUtil jsonUtil;
    @Autowired
    private OutboxEventService outboxEventService;

    @Test
    public void test() {
        OutboxEvent saved = outboxEventService.save(OutboxEvent.builder().outboxEventType(OutboxEventType.MEMBER).outboxStatus(OutboxStatus.PENDING).aggregateId(1L).payload(jsonUtil.toJson("payload")).build());

        System.out.println(saved.getId());
    }
}
