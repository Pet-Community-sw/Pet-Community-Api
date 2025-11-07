package com.example.petapp.domain.chatting.model.dto;

import com.example.petapp.domain.chatting.model.entity.ChatMessage;
import com.example.petapp.domain.chatting.model.type.CommandType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChatMessageDto {

    private CommandType commandType;
    private Long chatRoomId;
    private String message;
    private int seq;

    public void checkSeq(ChatMessage chatMessage) {
        if (seq != 0) {
            chatMessage.updateSeq(seq);
        }
    }
}
