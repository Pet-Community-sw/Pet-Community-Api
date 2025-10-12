package com.example.petapp.domain.chatting.model.entity;

import com.example.petapp.domain.chatting.model.type.ChatRoomType;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Set;

@Document(collection = "chat")
@CompoundIndexes({
        @CompoundIndex(name = "chat_room_seq_idx", def = "{'chatRoomId': 1, 'seq': 1}")
})//복합 인덱스 1:오름차순, -1:내림차순
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Id
    private String id;
    private ChatRoomType chatRoomType;
    private Long chatRoomId;
    private Long senderId;
    private String senderName;
    private String senderImageUrl;
    private String message;
    private Set<Long> users;
    private int UnReadCount;
    private int seq;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime messageTime;//포맷 필요함.

    public void updateSeq(int newSeq) {
        seq = newSeq;
    }

}
