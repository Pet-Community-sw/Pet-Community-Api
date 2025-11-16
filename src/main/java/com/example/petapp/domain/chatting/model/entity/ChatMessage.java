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
        @CompoundIndex(name = "chat_room_seq_idx", def = "{'chatRoomId': 1, 'seq': 1}"),
        @CompoundIndex(name = "client_message_id_and_sender_id_idx", def = "{'clientMessageId': 1, 'senderId': 1}",
                unique = true)//clientMessageId 와 senderId 유니크로 둠으로써 중복 저장 방지
})//복합 인덱스 1:오름차순, -1:내림차순

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Id
    private String id;
    private String clientMessageId;//client는 이 값을 보고 유저가 보낸건지 확인 후 필요한 부가 기능만 사용하면 될 듯.
    private ChatRoomType chatRoomType;
    private Long chatRoomId;
    private Long senderId;
    private String senderName;
    private String senderImageUrl;
    private String message;
    private Set<Long> users;
    private int UnReadCount;
    private Long seq;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime messageTime;//포맷 필요함.

    public void updateSeq(Long newSeq) {
        seq = newSeq;
    }
}
