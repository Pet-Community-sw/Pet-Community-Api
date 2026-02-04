package com.example.petapp.infrastructure.mq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitRetryHandler {

    private final RabbitTemplate rabbitTemplate;

    /**
     * 재시도 및 DLQ 전략 처리
     */
    public void handle(Object object, Message message, Exception e) {
        Map<String, Object> headerMap = message.getMessageProperties().getHeaders();
        int retryCount = (int) headerMap.getOrDefault("x-retry-count", 0);
        log.info("재시도 횟수 : {}", retryCount);
        //현재 몇 번째 재시도인지 헤더에서 확인
        String routingKey = message.getMessageProperties().getReceivedRoutingKey(); //해당 메시지의 원래 키

        if (retryCount == 0) {
            log.info("5초 대기 큐로 이동");
            sendToWaitQueue(object, "5s", routingKey, retryCount);
        } else if (retryCount == 1) {
            log.info("30초 대기 큐로 이동");
            sendToWaitQueue(object, "30s", routingKey, retryCount);
        } else {
            // 3회차 이상 실패 시 DLQ로 이동
            log.error("메일 전송 실패 error: {}", e.getMessage());
            rabbitTemplate.convertAndSend(RabbitConfig.DLX_EXCHANGE, "dead.letter", object);
        }
    }


    /**
     * 재시도 전용 큐로 전송
     */
    private void sendToWaitQueue(Object payload, String waitKey, String originalRoutingKey, int retryCount) {
        rabbitTemplate.convertAndSend(RabbitConfig.RETRY_EXCHANGE, waitKey, payload, msg -> {
            msg.getMessageProperties().getHeaders().put("x-retry-count", retryCount + 1);
            //재시도 횟수 저장

            msg.getMessageProperties().getHeaders().put("x-orig-routing-key", originalRoutingKey);
            //원래 라우팅 키 저장해서 재발행 큐에서 사용하기 위함
            return msg;
        });
    }
}
