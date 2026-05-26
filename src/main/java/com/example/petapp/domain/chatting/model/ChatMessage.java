package com.example.petapp.domain.chatting.model;

import com.example.petapp.application.usecase.chatting.model.type.ChatRoomType;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "chat_messages",
        indexes = {
                @Index(name = "idx_chat_messages_room_seq", columnList = "chat_room_id, seq")
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_chat_messages_client_message_sender",
                        columnNames = {"client_message_id", "sender_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_message_id", nullable = false)
    private String clientMessageId;//client는 이 값을 보고 유저가 보낸건지 확인 후 필요한 부가 기능만 사용하면 될 듯.

    @Enumerated(EnumType.STRING)
    @Column(name = "chat_room_type", nullable = false)
    private ChatRoomType chatRoomType;

    @Column(name = "chat_room_id", nullable = false)
    private Long chatRoomId;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(name = "sender_name", nullable = false)
    private String senderName;

    @Column(name = "sender_image_url")
    private String senderImageUrl;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false)
    private Long seq;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Column(name = "message_time", nullable = false)
    private LocalDateTime messageTime;//포맷 필요함.

    public void updateSeq(Long newSeq) {
        seq = newSeq;
    }
}
