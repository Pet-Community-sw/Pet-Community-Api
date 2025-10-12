package com.example.petapp.domain.chatting.reader;

import com.example.petapp.domain.groupchatroom.model.dto.response.ChatMessageResponseDto;
import org.springframework.stereotype.Service;

@Service
public interface ChattingReader {

    ChatMessageResponseDto getMessages(Long chatRoomId, Long userId, int page);

}
