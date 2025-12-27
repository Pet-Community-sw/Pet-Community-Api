package com.example.petapp.application.in.chatting;

import com.example.petapp.application.in.chatroom.dto.response.ChatMessageResponseDto;

public interface ReaderUseCase {

    ChatMessageResponseDto getMessages(Long chatRoomId, Long userId, int page);

    ChatMessageResponseDto getAfterMessages(Long chatRoomId, Long lastSeq, Long userId);
}
