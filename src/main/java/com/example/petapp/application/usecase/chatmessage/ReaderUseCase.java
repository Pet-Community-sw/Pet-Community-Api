package com.example.petapp.application.usecase.chatmessage;

import com.example.petapp.application.usecase.chatroom.dto.response.ChatMessageResponseDto;
import com.example.petapp.domain.chatroom.model.ChatRoom;

public interface ReaderUseCase {

    ChatMessageResponseDto getMessages(ChatRoom chatRoom, Long userId, int page);

    ChatMessageResponseDto getAfterMessages(ChatRoom chatRoom, Long lastSeq, Long userId);
}
