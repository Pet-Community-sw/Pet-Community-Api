package com.example.petapp.infrastructure.database.mongo;

import com.example.petapp.domain.chatting.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MongoService {

    private final MongoTemplate mongoTemplate;

    /**
     * (startSeq, endSeq] 범위의 메시지에서
     * users 배열에 userId가 들어있는 문서만 대상으로
     * users 에서 userId를 제거하고, chatUnReadCount 를 1 감소시킨다.
     * 동시성 안전
     */
    public void updateMessages(Long chatRoomId, Long userId, Long startSeq, Long endSeq) {
        if (endSeq <= startSeq) return;

        Query query = new Query(
                Criteria.where("chatRoomId").is(chatRoomId)
                        .and("seq").gt(startSeq).lte(endSeq)   // (startSeq, endSeq]
                        .and("users").in(userId)               // 아직 미읽음 대상에 자신이 있을 때만
                        .and("chatUnReadCount").gt(0)          // 음수 방지
        );

        Update update = new Update()
                .pull("users", userId)                     // users 배열에서 자신 제거
                .inc("chatUnReadCount", -1);               // count -1

        mongoTemplate.updateMulti(query, update, ChatMessage.class)
                .getModifiedCount();
    }
}

