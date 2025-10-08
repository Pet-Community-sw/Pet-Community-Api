package com.example.PetApp.domain.chatting.reader;

import com.example.PetApp.domain.groupchatroom.model.dto.response.ChatMessageResponseDto;
import org.springframework.stereotype.Service;

@Service
public interface ChattingReader {

    ChatMessageResponseDto getMessages(Long chatRoomId, Long userId, int page);

}
