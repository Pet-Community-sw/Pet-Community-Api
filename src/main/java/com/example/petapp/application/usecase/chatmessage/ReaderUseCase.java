package com.example.petapp.application.usecase.chatmessage;

import com.example.petapp.application.usecase.chatroom.dto.response.ChatMessageResponseDto;

public interface ReaderUseCase {

    ChatMessageResponseDto getMessages(Long chatRoomId, Long userId, int page);

    ChatMessageResponseDto getAfterMessages(Long chatRoomId, Long lastSeq, Long userId);
}
